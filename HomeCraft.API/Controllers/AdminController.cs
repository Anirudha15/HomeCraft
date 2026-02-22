using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;

[Authorize(Roles = "Admin")]
[ApiController]
[Route("api/admin")]
public class AdminController : ControllerBase
{
    private readonly Db _db;

    public AdminController(IConfiguration config)
    {
        _db = new Db(config);
    }

    #region CUSTOMERS
    [HttpGet("customers")]
    public IActionResult GetCustomers()
    {
        var list = new List<object>();
        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            "SELECT id, name, phone, email FROM dbo.customer ORDER BY id", con);

        var reader = cmd.ExecuteReader();
        while (reader.Read())
        {
            list.Add(new
            {
                id = reader["id"],
                name = reader["name"],
                phone = reader["phone"],
                email = reader["email"]
            });
        }

        return Ok(list);
    }

    [HttpDelete("customer/{id}")]
    public IActionResult DeleteCustomer(int id)
    {
        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            "DELETE FROM dbo.customer WHERE id=@id", con);
        cmd.Parameters.AddWithValue("@id", id);

        cmd.ExecuteNonQuery();
        return Ok();
    }
    #endregion

    #region SELLERS
    [HttpGet("sellers/pending")]
    public IActionResult PendingSellers()
    {
        var list = new List<object>();
        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            @"SELECT id, name, phone, email, license_number, profile_image, aadhar_image
              FROM dbo.seller WHERE is_verified = 0", con);

        var reader = cmd.ExecuteReader();
        while (reader.Read())
        {
            list.Add(new
            {
                id = reader["id"],
                name = reader["name"],
                phone = reader["phone"],
                email = reader["email"],
                license = reader["license_number"],
                profile = reader["profile_image"],
                aadhar = reader["aadhar_image"]
            });
        }

        return Ok(list);
    }

    [HttpPost("seller/accept/{id}")]
    public IActionResult AcceptSeller(int id)
    {
        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            "UPDATE dbo.seller SET is_verified = 1 WHERE id=@id", con);
        cmd.Parameters.AddWithValue("@id", id);

        cmd.ExecuteNonQuery();
        return Ok();
    }

    [HttpPost("seller/reject/{id}")]
    public IActionResult RejectSeller(int id)
    {
        using var con = _db.GetConnection();
        con.Open();

        var fetchCmd = new SqlCommand(
            "SELECT email, password_hash FROM dbo.seller WHERE id=@id", con);
        fetchCmd.Parameters.AddWithValue("@id", id);

        using var reader = fetchCmd.ExecuteReader();
        if (!reader.Read()) return NotFound();

        var email = reader["email"].ToString();
        var passwordHash = reader["password_hash"].ToString();
        reader.Close();

        var insertCmd = new SqlCommand(
            @"INSERT INTO dbo.seller_rejected (email, password_hash)
          VALUES (@e, @p)", con);

        insertCmd.Parameters.AddWithValue("@e", email);
        insertCmd.Parameters.AddWithValue("@p", passwordHash);
        insertCmd.ExecuteNonQuery();

        var deleteCmd = new SqlCommand(
            "DELETE FROM dbo.seller WHERE id=@id", con);
        deleteCmd.Parameters.AddWithValue("@id", id);
        deleteCmd.ExecuteNonQuery();

        return Ok();
    }


    [HttpGet("sellers/confirmed")]
    public IActionResult ConfirmedSellers()
    {
        var list = new List<object>();
        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            @"SELECT id, name, phone, email, license_number, profile_image
              FROM dbo.seller WHERE is_verified = 1", con);

        var reader = cmd.ExecuteReader();
        while (reader.Read())
        {
            list.Add(new
            {
                id = reader["id"],
                name = reader["name"],
                phone = reader["phone"],
                email = reader["email"],
                license = reader["license_number"],
                profile = reader["profile_image"]
            });
        }

        return Ok(list);
    }

    [HttpDelete("seller/{id}")]
    public IActionResult DeleteSeller(int id)
    {
        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            "DELETE FROM dbo.seller WHERE id=@id", con);
        cmd.Parameters.AddWithValue("@id", id);

        cmd.ExecuteNonQuery();
        return Ok();
    }

    [HttpGet("sellers/rejected")]
    public IActionResult RejectedSellers()
    {
        var list = new List<object>();
        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            "SELECT email FROM dbo.seller_rejected ORDER BY id", con);

        var reader = cmd.ExecuteReader();
        while (reader.Read())
        {
            list.Add(new { email = reader["email"] });
        }

        return Ok(list);
    }
    #endregion
    [AllowAnonymous]
    [HttpGet("reset-password")]
    public IActionResult ResetPassword()
    {
        using var con = _db.GetConnection();
        con.Open();
        string email = "admin@gmail.com";
        string password = "admin123";
        string hash = BCrypt.Net.BCrypt.HashPassword(password);
        
        var checkCmd = new SqlCommand("SELECT count(*) FROM dbo.admin WHERE email = @e", con);
        checkCmd.Parameters.AddWithValue("@e", email);
        int count = (int)checkCmd.ExecuteScalar();

        if (count > 0)
        {
            var updateCmd = new SqlCommand("UPDATE dbo.admin SET password_hash = @p WHERE email = @e", con);
            updateCmd.Parameters.AddWithValue("@p", hash);
            updateCmd.Parameters.AddWithValue("@e", email);
            updateCmd.ExecuteNonQuery();
            return Ok("Admin password reset to 'admin123'");
        }
        else
        {
            var insertCmd = new SqlCommand("INSERT INTO dbo.admin (email, password_hash) VALUES (@e, @p)", con);
            insertCmd.Parameters.AddWithValue("@e", email);
            insertCmd.Parameters.AddWithValue("@p", hash);
            insertCmd.ExecuteNonQuery();
            return Ok("Admin created with password 'admin123'");
        }
    }
}
