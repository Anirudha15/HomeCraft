using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
    private readonly IConfiguration _config;
    private readonly IAuthService _authService;

    public AuthController(IConfiguration config, IAuthService authService)
    {
        _config = config;
        _authService = authService;
    }

    [HttpPost("login")]
    public IActionResult Login(LoginDTO dto)
    {
        var result = _authService.Login(dto.Email, dto.Password);

        if (!result.Success)
            return Unauthorized(new { message = result.ErrorMessage });

        var token = JwtHelper.GenerateToken(
            result.UserId,
            result.Role,
            result.Email,
            _config);

        return Ok(new { token, role = result.Role });
    }
}
