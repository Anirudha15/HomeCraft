using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;
using System.Security.Claims;

namespace HomeCraft.API.Controllers;

[ApiController]
[Route("api/workshop")]
public class WorkshopController : ControllerBase
{
    private readonly Db _db;

    public WorkshopController(IConfiguration config)
    {
        _db = new Db(config);
    }

    [HttpGet("all")]
    public IActionResult GetAll()
    {
        var list = new List<object>();
        using var con = _db.GetConnection();
        con.Open();
        var cmd = new SqlCommand("SELECT w.*, s.name as seller_name FROM dbo.workshop w JOIN dbo.seller s ON w.seller_id = s.id", con);
        using var reader = cmd.ExecuteReader();
        while (reader.Read())
        {
            list.Add(new
            {
                id = reader["id"],
                seller_id = reader["seller_id"],
                seller_name = reader["seller_name"],
                topic = reader["topic"],
                description = reader["description"],
                venue = reader["venue"],
                timing = reader["timing"],
                capacity = reader["capacity"],
                is_paid = reader["is_paid"]
            });
        }
        return Ok(list);
    }

    [Authorize(Roles = "Seller")]
    [HttpPost("create")]
    public IActionResult Create([FromBody] WorkshopDto dto)
    {
        // Securely get ID from token
        int sellerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();
        
        var cmd = new SqlCommand(@"
            INSERT INTO dbo.workshop (seller_id, topic, description, venue, timing, capacity, is_paid)
            VALUES (@s, @t, @d, @v, @time, @c, @p)", con);
        
        cmd.Parameters.AddWithValue("@s", sellerId);
        cmd.Parameters.AddWithValue("@t", dto.Topic);
        cmd.Parameters.AddWithValue("@d", dto.Description);
        cmd.Parameters.AddWithValue("@v", dto.Venue);
        cmd.Parameters.AddWithValue("@time", dto.Timing);
        cmd.Parameters.AddWithValue("@c", dto.Capacity);
        cmd.Parameters.AddWithValue("@p", dto.IsPaid);
        
        cmd.ExecuteNonQuery();
        return Ok(new { message = "Workshop created successfully" });
    }

    public class WorkshopDto
    {
        public int SellerId { get; set; }
        public string Topic { get; set; }
        public string Description { get; set; }
        public string Venue { get; set; }
        public DateTime Timing { get; set; }
        public int Capacity { get; set; }
        public bool IsPaid { get; set; }
    }
}
