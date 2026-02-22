using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;
using BCrypt.Net;

[ApiController]
[Route("api/register")]
public class RegisterController : ControllerBase
{
    private readonly Db _db;

    public RegisterController(IConfiguration config)
    {
        _db = new Db(config);
    }

    #region CUSTOMER
    [HttpPost("customer")]
    [Consumes("multipart/form-data")]
    public IActionResult RegisterCustomer([FromForm] RegisterCustomerDTO dto)
    {
        if (string.IsNullOrWhiteSpace(dto.Phone) || dto.Phone.Length != 10)
            return BadRequest("Phone number must be exactly 10 digits");

        if (string.IsNullOrWhiteSpace(dto.Interests))
            return BadRequest("At least one interest must be selected");
 
        if (!ValidationHelper.IsValidEmail(dto.Email))
            return BadRequest("Invalid email format");

        if (dto.Password.Length < 6)
            return BadRequest("Password must be at least 6 characters");

        var uploadsPath = Path.Combine(Directory.GetCurrentDirectory(), "uploads/customers");
        Directory.CreateDirectory(uploadsPath);

        var fileName = $"{Guid.NewGuid()}_{dto.ProfileImage.FileName}";
        var filePath = Path.Combine(uploadsPath, fileName);

        using (var stream = new FileStream(filePath, FileMode.Create))
        {
            dto.ProfileImage.CopyTo(stream);
        }

        var imageDbPath = $"/uploads/customers/{fileName}";

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            @"INSERT INTO dbo.customer
          (name, location, interests, email, password_hash, phone, profile_image)
          VALUES (@n, @l, @i, @e, @p, @ph, @img)", con);

        cmd.Parameters.AddWithValue("@n", dto.Name);
        cmd.Parameters.AddWithValue("@l", dto.Location);
        cmd.Parameters.AddWithValue("@i", dto.Interests);
        cmd.Parameters.AddWithValue("@e", dto.Email);
        cmd.Parameters.AddWithValue("@ph", dto.Phone);
        cmd.Parameters.AddWithValue("@img", imageDbPath);

        var hashedPassword = BCrypt.Net.BCrypt.HashPassword(dto.Password);
        cmd.Parameters.AddWithValue("@p", hashedPassword);

        try
        {
            cmd.ExecuteNonQuery();
        }
        catch (SqlException)
        {
            return BadRequest("Email already exists");
        }

        return Ok("Customer registered");
    }
    #endregion

    #region SELLER
    [HttpPost("seller")]
    [Consumes("multipart/form-data")]
    public IActionResult RegisterSeller([FromForm] RegisterSellerDTO dto)
    {
        if (string.IsNullOrWhiteSpace(dto.Phone) || dto.Phone.Length != 10)
            return BadRequest("Phone number must be exactly 10 digits");

        if (string.IsNullOrWhiteSpace(dto.Craft))
            return BadRequest("At least one craft must be selected");

        if (!ValidationHelper.IsValidEmail(dto.Email))
            return BadRequest("Invalid email format");

        if (dto.Password.Length < 6)
            return BadRequest("Password must be at least 6 characters");

        var sellerUploads = Path.Combine(Directory.GetCurrentDirectory(), "uploads/sellers");
        Directory.CreateDirectory(sellerUploads);

        var profileFileName = $"{Guid.NewGuid()}_{dto.ProfileImage.FileName}";
        var profilePath = Path.Combine(sellerUploads, profileFileName);

        using (var stream = new FileStream(profilePath, FileMode.Create))
        {
            dto.ProfileImage.CopyTo(stream);
        }

        var aadharUploads = Path.Combine(sellerUploads, "aadhar");
        Directory.CreateDirectory(aadharUploads);

        var aadharFileName = $"{Guid.NewGuid()}_{dto.AadharImage.FileName}";
        var aadharPath = Path.Combine(aadharUploads, aadharFileName);

        using (var stream = new FileStream(aadharPath, FileMode.Create))
        {
            dto.AadharImage.CopyTo(stream);
        }

        var profileDbPath = $"/uploads/sellers/{profileFileName}";
        var aadharDbPath = $"/uploads/sellers/aadhar/{aadharFileName}";

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            @"INSERT INTO dbo.seller
          (name, location, craft, email, password_hash, phone,
           profile_image, license_number, aadhar_image, is_verified)
          VALUES
          (@n, @l, @c, @e, @p, @ph, @img, @lic, @aadhar, 0)", con);

        cmd.Parameters.AddWithValue("@n", dto.Name);
        cmd.Parameters.AddWithValue("@l", dto.Location);
        cmd.Parameters.AddWithValue("@c", dto.Craft);
        cmd.Parameters.AddWithValue("@e", dto.Email);
        cmd.Parameters.AddWithValue("@ph", dto.Phone);
        cmd.Parameters.AddWithValue("@img", profileDbPath);
        cmd.Parameters.AddWithValue("@lic", dto.LicenseNumber);
        cmd.Parameters.AddWithValue("@aadhar", aadharDbPath);

        var hashedPassword = BCrypt.Net.BCrypt.HashPassword(dto.Password);
        cmd.Parameters.AddWithValue("@p", hashedPassword);

        try
        {
            cmd.ExecuteNonQuery();
        }
        catch (SqlException)
        {
            return BadRequest("Email already exists");
        }

        return Ok("Seller registered. Pending admin verification.");
    }
    #endregion
}
