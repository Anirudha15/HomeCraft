using Microsoft.Data.SqlClient;

public class AuthService : IAuthService
{
    private readonly Db _db;

    public AuthService(IConfiguration config)
    {
        _db = new Db(config);
    }

    public AuthResult Login(string email, string password)
    {
        using var con = _db.GetConnection();
        con.Open();

        var rejectedCmd = new SqlCommand(
            "SELECT password_hash FROM dbo.seller_rejected WHERE email = @e", con);
        rejectedCmd.Parameters.AddWithValue("@e", email);

        using (var reader = rejectedCmd.ExecuteReader())
        {
            if (reader.Read())
            {
                string hash = reader.GetString(0);

                if (BCrypt.Net.BCrypt.Verify(password, hash))
                {
                    return Fail("Registration rejected. Re-create account.");
                }
                else
                {
                    return Fail("Invalid email or password");
                }
            }
        }

        var customerCmd = new SqlCommand(
            "SELECT id, password_hash FROM dbo.customer WHERE email = @e", con);
        customerCmd.Parameters.AddWithValue("@e", email);

        using (var reader = customerCmd.ExecuteReader())
        {
            if (reader.Read())
            {
                int userId = reader.GetInt32(0);
                string hash = reader.GetString(1);

                if (!BCrypt.Net.BCrypt.Verify(password, hash))
                    return Fail("Invalid email or password");

                return Success(userId, "Customer", email);
            }
        }

        var sellerCmd = new SqlCommand(
            @"SELECT id, password_hash, is_verified
              FROM dbo.seller WHERE email = @e", con);
        sellerCmd.Parameters.AddWithValue("@e", email);

        using (var reader = sellerCmd.ExecuteReader())
        {
            if (reader.Read())
            {
                int userId = reader.GetInt32(0);
                string hash = reader.GetString(1);
                bool verified = reader.GetBoolean(2);

                if (!BCrypt.Net.BCrypt.Verify(password, hash))
                    return Fail("Invalid email or password");

                if (!verified)
                    return Fail("Pending verification from admin.");

                return Success(userId, "Seller", email);
            }
        }

        var adminCmd = new SqlCommand(
            "SELECT id, password_hash FROM dbo.admin WHERE email = @e", con);
        adminCmd.Parameters.AddWithValue("@e", email);

        using (var reader = adminCmd.ExecuteReader())
        {
            if (reader.Read())
            {
                int userId = reader.GetInt32(0);
                string hash = reader.GetString(1);

                if (!BCrypt.Net.BCrypt.Verify(password, hash))
                    return Fail("Invalid admin credentials");

                return Success(userId, "Admin", email);
            }
        }

        return Fail("Invalid email or password");
    }

    private AuthResult Success(int userId, string role, string email)
    {
        return new AuthResult
        {
            Success = true,
            UserId = userId,
            Role = role,
            Email = email
        };
    }

    private AuthResult Fail(string message)
    {
        return new AuthResult
        {
            Success = false,
            ErrorMessage = message
        };
    }
}
