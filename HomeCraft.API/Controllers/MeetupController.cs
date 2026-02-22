using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;

namespace HomeCraft.API.Controllers;

[ApiController]
[Route("api/meetup")]
public class MeetupController : ControllerBase
{
    private readonly Db _db;

    public MeetupController(IConfiguration config)
    {
        _db = new Db(config);
    }

    [HttpGet("all")]
    public IActionResult GetAll()
    {
        var list = new List<object>();
        using var con = _db.GetConnection();
        con.Open();
        var cmd = new SqlCommand("SELECT * FROM dbo.meetup", con);
        using var reader = cmd.ExecuteReader();
        while (reader.Read())
        {
            list.Add(new
            {
                id = reader["id"],
                theme = reader["theme"],
                location = reader["location"],
                timing = reader["timing"]
            });
        }
        return Ok(list);
    }

    [Authorize(Roles = "Admin")]
    [HttpPost("create")]
    public IActionResult Create([FromBody] MeetupDto dto)
    {
        using var con = _db.GetConnection();
        con.Open();
        
        var cmd = new SqlCommand(@"
            INSERT INTO dbo.meetup (theme, location, timing)
            VALUES (@t, @l, @time)", con);
        
        cmd.Parameters.AddWithValue("@t", dto.Theme);
        cmd.Parameters.AddWithValue("@l", dto.Location);
        cmd.Parameters.AddWithValue("@time", dto.Timing);
        
        cmd.ExecuteNonQuery();
        return Ok(new { message = "Meetup created successfully" });
    }

    public class MeetupDto
    {
        public string Theme { get; set; }
        public string Location { get; set; }
        public DateTime Timing { get; set; }
    }
}
