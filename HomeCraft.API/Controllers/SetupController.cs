using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;

[Route("api/setup")]
[ApiController]
public class SetupController : ControllerBase
{
    private readonly Db _db;

    public SetupController(IConfiguration config)
    {
        _db = new Db(config);
    }

    [HttpGet("increase-status-col")]
    public IActionResult IncreaseStatusColumn()
    {
        using var con = _db.GetConnection();
        con.Open();
        try
        {
            var cmd = new SqlCommand("ALTER TABLE dbo.orders ALTER COLUMN status VARCHAR(50)", con);
            cmd.ExecuteNonQuery();
            return Ok("Status column size updated.");
        }
        catch (Exception ex)
        {
             return BadRequest(ex.Message);
        }
    }
}
