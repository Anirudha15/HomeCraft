using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;
using System.Security.Claims;

[Authorize(Roles = "Customer")]
[ApiController]
[Route("api/customer")]
public class CustomerController : ControllerBase
{
    private readonly Db _db;

    public CustomerController(IConfiguration config)
    {
        _db = new Db(config);
    }

    #region USER DETAILS
    [HttpGet("profile")]
    public IActionResult GetProfile()
    {
        int customerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            @"SELECT name, phone, email, location, interests, profile_image
              FROM dbo.customer WHERE id=@id", con);

        cmd.Parameters.AddWithValue("@id", customerId);

        using var r = cmd.ExecuteReader();
        if (!r.Read()) return NotFound();

        return Ok(new
        {
            name = r.GetString(0),
            phone = r.GetString(1),
            email = r.GetString(2),
            location = r.GetString(3),
            interests = r.GetString(4),
            profileImage = r.GetString(5)
        });
    }
    #endregion

    #region CATALOGUE
    [HttpGet("products")]
    public IActionResult GetProducts(
    string? category,
    string? type,
    string? location)
    {
        using var con = _db.GetConnection();
        con.Open();

        var sql = @"
        SELECT 
            p.id, p.name, p.description, p.image_path,
            p.type, p.price, p.customizations,
            s.name AS seller_name, p.location
        FROM dbo.product p
        JOIN dbo.seller s ON p.seller_id = s.id
        WHERE 1=1";

        if (!string.IsNullOrEmpty(category))
            sql += " AND p.category=@c";

        if (!string.IsNullOrEmpty(type))
            sql += " AND p.type=@t";

        if (!string.IsNullOrEmpty(location))
            sql += " AND p.location=@l";

        var cmd = new SqlCommand(sql, con);

        if (!string.IsNullOrEmpty(category))
            cmd.Parameters.AddWithValue("@c", category);
        if (!string.IsNullOrEmpty(type))
            cmd.Parameters.AddWithValue("@t", type);
        if (!string.IsNullOrEmpty(location))
            cmd.Parameters.AddWithValue("@l", location);

        var list = new List<object>();
        using var r = cmd.ExecuteReader();
        while (r.Read())
        {
            list.Add(new
            {
                id = r.GetInt32(0),
                name = r.GetString(1),
                description = r.GetString(2),
                image = r.GetString(3),
                type = r.GetString(4),
                price = r.GetDecimal(5),
                customizations = r.IsDBNull(6) ? "None" : r.GetString(6),
                seller = r.GetString(7),
                location = r.GetString(8)
            });
        }

        return Ok(list);
    }

    [HttpGet("product-locations")]
    public IActionResult GetLocations()
    {
        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            "SELECT DISTINCT location FROM dbo.product", con);

        var list = new List<string>();
        using var r = cmd.ExecuteReader();
        while (r.Read())
            list.Add(r.GetString(0));

        return Ok(list);
    }

    [HttpPost("cart/add")]
    public IActionResult AddToCart(int productId, int quantity)
    {
        int customerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();

        var checkCmd = new SqlCommand(@"
        SELECT id, quantity FROM dbo.orders
        WHERE customer_id=@c AND product_id=@p AND status='Pending'", con);

        checkCmd.Parameters.AddWithValue("@c", customerId);
        checkCmd.Parameters.AddWithValue("@p", productId);

        using var r = checkCmd.ExecuteReader();
        if (r.Read())
        {
            int orderId = r.GetInt32(0);
            int existingQty = r.GetInt32(1);
            r.Close();

            var updateCmd = new SqlCommand(
                "UPDATE dbo.orders SET quantity=@q WHERE id=@id", con);
            updateCmd.Parameters.AddWithValue("@q", existingQty + quantity);
            updateCmd.Parameters.AddWithValue("@id", orderId);
            updateCmd.ExecuteNonQuery();

            return Ok();
        }
        r.Close();

        var insertCmd = new SqlCommand(@"
        INSERT INTO dbo.orders
        (product_id, customer_id, seller_id, name, price, quantity, customizations)
        SELECT p.id, @c, p.seller_id, p.name, p.price, @q, p.customizations
        FROM dbo.product p WHERE p.id=@p", con);

        insertCmd.Parameters.AddWithValue("@c", customerId);
        insertCmd.Parameters.AddWithValue("@p", productId);
        insertCmd.Parameters.AddWithValue("@q", quantity);
        insertCmd.ExecuteNonQuery();

        return Ok();
    }
    #endregion

    #region CART
    [HttpGet("cart")]
    public IActionResult GetCart()
    {
        int customerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            "SELECT * FROM dbo.orders WHERE customer_id=@c AND status='Pending'", con);
        cmd.Parameters.AddWithValue("@c", customerId);

        var list = new List<Order>();
        using var r = cmd.ExecuteReader();
        while (r.Read())
        {
            list.Add(new Order
            {
                Id = r.GetInt32(0),
                ProductId = r.GetInt32(1),
                CustomerId = r.GetInt32(2),
                SellerId = r.GetInt32(3),
                Name = r.GetString(4),
                Price = r.GetDecimal(5),
                Quantity = r.GetInt32(6),
                Customizations = r.IsDBNull(7) ? null : r.GetString(7),
                Status = r.GetString(8)
            });
        }

        return Ok(list);
    }

    [HttpPut("cart/qty")]
    public IActionResult UpdateQty(int orderId, int delta)
    {
        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(@"
        UPDATE dbo.orders
        SET quantity = quantity + @d
        WHERE id=@id AND quantity + @d >= 1", con);

        cmd.Parameters.AddWithValue("@id", orderId);
        cmd.Parameters.AddWithValue("@d", delta);
        cmd.ExecuteNonQuery();

        return Ok();
    }

    [HttpDelete("cart/{id}")]
    public IActionResult RemoveFromCart(int id)
    {
        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            "DELETE FROM dbo.orders WHERE id=@id", con);
        cmd.Parameters.AddWithValue("@id", id);
        cmd.ExecuteNonQuery();

        return Ok();
    }

    [HttpPost("customize")]
    public IActionResult Customize(int orderId, string info)
    {
        int customerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(@"
        IF EXISTS (SELECT 1 FROM dbo.cust_request WHERE orders_id=@o)
            UPDATE dbo.cust_request SET info=@i, status='Pending' WHERE orders_id=@o
        ELSE
            INSERT INTO dbo.cust_request(orders_id, product_id, customer_id, seller_id, info, status)
            SELECT @o, product_id, customer_id, seller_id, @i, 'Pending'
            FROM dbo.orders WHERE id=@o
        
        -- Move order out of cart to wait for approval
        UPDATE dbo.orders SET status='CustomizationPending' WHERE id=@o
        ", con);

        cmd.Parameters.AddWithValue("@o", orderId);
        cmd.Parameters.AddWithValue("@i", info);
        cmd.ExecuteNonQuery();

        return Ok();
    }

    [HttpGet("requests")]
    public IActionResult GetMyRequests()
    {
        int customerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);
        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(@"
            SELECT r.id, p.name, r.info, r.status
            FROM dbo.cust_request r
            JOIN dbo.product p ON r.product_id = p.id
            WHERE r.customer_id = @cid", con);
        
        cmd.Parameters.AddWithValue("@cid", customerId);
        
        var list = new List<object>();
        using var reader = cmd.ExecuteReader();
        while(reader.Read())
        {
            list.Add(new {
                id = reader["id"],
                product_name = reader["name"],
                info = reader["info"],
                status = reader["status"]
            });
        }
        return Ok(list);
    }

    [HttpPost("order/confirm")]
    public IActionResult ConfirmOrder()
    {
        int customerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            "UPDATE dbo.orders SET status='Confirmed' WHERE customer_id=@c", con);
        cmd.Parameters.AddWithValue("@c", customerId);
        cmd.ExecuteNonQuery();

        return Ok();
    }

    [HttpPost("order/cancel")]
    public IActionResult CancelOrder()
    {
        int customerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            "UPDATE dbo.orders SET status='Pending' WHERE customer_id=@c", con);
        cmd.Parameters.AddWithValue("@c", customerId);
        cmd.ExecuteNonQuery();

        return Ok();
    }

    [HttpGet("orders/confirmed")]
    public IActionResult GetConfirmedOrders()
    {
        int customerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            "SELECT * FROM dbo.orders WHERE customer_id=@c AND status='Confirmed'",
            con);
        cmd.Parameters.AddWithValue("@c", customerId);

        var list = new List<Order>();
        using var r = cmd.ExecuteReader();
        while (r.Read())
        {
            list.Add(new Order
            {
                Id = r.GetInt32(0),
                ProductId = r.GetInt32(1),
                CustomerId = r.GetInt32(2),
                SellerId = r.GetInt32(3),
                Name = r.GetString(4),
                Price = r.GetDecimal(5),
                Quantity = r.GetInt32(6),
                Customizations = r.IsDBNull(7) ? null : r.GetString(7),
                Status = r.GetString(8)
            });
        }

        return Ok(list);
    }
    #endregion
}
