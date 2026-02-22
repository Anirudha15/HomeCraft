public class AuthResult
{
    public bool Success { get; set; }
    public int UserId { get; set; }
    public string Role { get; set; }
    public string Email { get; set; }
    public string ErrorMessage { get; set; }
}
