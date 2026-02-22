using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;

namespace HomeCraft.API.Controllers;

[ApiController]
[Route("api/customization")]
public class CustomizationController : ControllerBase
{
    private readonly Db _db;

    public CustomizationController(IConfiguration config)
    {
        _db = new Db(config);
    }

    [Authorize(Roles = "Seller")]
    [HttpGet("seller/pending/{sellerId}")]
    public IActionResult GetPendingRequests(int sellerId)
    {
        var list = new List<object>();
        using var con = _db.GetConnection();
        con.Open();

        // Join to get customer name details if needed
        var cmd = new SqlCommand(@"
            SELECT r.id, r.product_id, r.info, r.status, c.name as customer_name, p.name as product_name
            FROM dbo.cust_request r
            JOIN dbo.customer c ON r.customer_id = c.id
            JOIN dbo.product p ON r.product_id = p.id
            WHERE r.seller_id = @sid AND r.status = 'Pending'", con);
        
        cmd.Parameters.AddWithValue("@sid", sellerId);

        using var reader = cmd.ExecuteReader();
        while (reader.Read())
        {
            list.Add(new
            {
                id = reader["id"],
                product_id = reader["product_id"],
                product_name = reader["product_name"],
                customer_name = reader["customer_name"],
                info = reader["info"],
                status = reader["status"]
            });
        }
        return Ok(list);
    }

    [Authorize(Roles = "Seller")]
    [HttpPost("response")]
    public IActionResult SendResponse([FromBody] ResponseDto dto)
    {
        using var con = _db.GetConnection();
        con.Open();
        
        // Update request status
        var cmd = new SqlCommand("UPDATE dbo.cust_request SET status = @st WHERE id = @rid", con);
        cmd.Parameters.AddWithValue("@rid", dto.RequestId);
        cmd.Parameters.AddWithValue("@st", dto.Status); // "Confirmed" or "Rejected"
        cmd.ExecuteNonQuery();

        if (dto.Status == "Confirmed")
        {
            // Move order back to Cart (Pending)
            var upCmd = new SqlCommand(@"
                UPDATE dbo.orders 
                SET status='Pending' 
                WHERE id=(SELECT orders_id FROM dbo.cust_request WHERE id=@rid)", con);
            upCmd.Parameters.AddWithValue("@rid", dto.RequestId);
            upCmd.ExecuteNonQuery();
        }
        else if (dto.Status == "Rejected")
        {
             var upCmd = new SqlCommand(@"
                UPDATE dbo.orders 
                SET status='CustomizationRejected' 
                WHERE id=(SELECT orders_id FROM dbo.cust_request WHERE id=@rid)", con);
            upCmd.Parameters.AddWithValue("@rid", dto.RequestId);
            upCmd.ExecuteNonQuery();
        }

        return Ok(new { message = "Status updated" });
    }

    public class ResponseDto
    {
        public int RequestId { get; set; }
        public string Status { get; set; } // "Confirmed", "Rejected"
    }
}
