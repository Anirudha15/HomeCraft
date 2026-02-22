using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;
using System.Security.Claims;

[Authorize(Roles = "Seller")]
[ApiController]
[Route("api/seller")]
public class SellerController : ControllerBase
{
    private readonly Db _db;

    public SellerController(IConfiguration config)
    {
        _db = new Db(config);
    }

    #region USER DETAILS
    [HttpGet("profile")]
    public IActionResult GetProfile()
    {
        int sellerId = int.Parse(
            User.FindFirst(ClaimTypes.NameIdentifier)!.Value
        );

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            @"SELECT 
                name,
                phone,
                email,
                location,
                license_number,
                craft,
                profile_image
              FROM dbo.seller
              WHERE id = @id", con);

        cmd.Parameters.AddWithValue("@id", sellerId);

        using var reader = cmd.ExecuteReader();
        if (!reader.Read())
            return NotFound();

        return Ok(new
        {
            id = sellerId,
            name = reader.GetString(0),
            phone = reader.GetString(1),
            email = reader.GetString(2),
            location = reader.GetString(3),
            licenseNumber = reader.GetString(4),
            craft = reader.GetString(5),
            profileImage = reader.GetString(6)
        });
    }
    #endregion

    #region ADD PRODUCTS
    [HttpPost("product")]
    public IActionResult AddProduct([FromForm] AddProductDTO dto)
    {
        if (dto.Image == null)
            return BadRequest("Product image is required.");

        if (string.IsNullOrWhiteSpace(dto.Name))
            return BadRequest("Product name is required.");

        if (dto.Price == null || dto.Price <= 0)
            return BadRequest("Valid price is required.");

        if (string.IsNullOrWhiteSpace(dto.Category) || string.IsNullOrWhiteSpace(dto.Type))
            return BadRequest("Category and type are required.");

        int sellerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();

        var locCmd = new SqlCommand(
            "SELECT location FROM dbo.seller WHERE id=@id", con);
        locCmd.Parameters.AddWithValue("@id", sellerId);
        string location = (string)locCmd.ExecuteScalar();

        string imgPath = $"/uploads/sellers/product/image/{Guid.NewGuid()}_{dto.Image.FileName}";
        using (var fs = new FileStream(
            Path.Combine("uploads", "sellers", "product", "image", Path.GetFileName(imgPath)),
            FileMode.Create))
        {
            dto.Image.CopyTo(fs);
        }

        var cmd = new SqlCommand(
            @"INSERT INTO dbo.product
            (seller_id,name,description,image_path,
            category,type,price,customizations,location)
            VALUES(@sid,@n,@d,@i,@c,@t,@p,@cu,@l)", con);

        cmd.Parameters.AddWithValue("@sid", sellerId);
        cmd.Parameters.AddWithValue("@n", dto.Name);
        cmd.Parameters.AddWithValue("@d", dto.Description);
        cmd.Parameters.AddWithValue("@i", imgPath);
        cmd.Parameters.AddWithValue("@c", dto.Category);
        cmd.Parameters.AddWithValue("@t", dto.Type);
        cmd.Parameters.AddWithValue("@p", dto.Price);
        cmd.Parameters.AddWithValue("@cu",
            string.IsNullOrEmpty(dto.Customizations) ? DBNull.Value : dto.Customizations);
        cmd.Parameters.AddWithValue("@l", location);

        cmd.ExecuteNonQuery();
        return Ok();
    }


    [HttpGet("products")]
    public IActionResult GetProducts()
    {
        int sellerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            @"SELECT id,name,image_path,type,price,customizations
          FROM dbo.product
          WHERE seller_id=@id", con);

        cmd.Parameters.AddWithValue("@id", sellerId);

        var list = new List<object>();
        using var r = cmd.ExecuteReader();
        while (r.Read())
        {
            list.Add(new
            {
                id = r.GetInt32(0),
                name = r.GetString(1),
                image = r.GetString(2),
                type = r.GetString(3),
                price = r.GetDecimal(4),
                customizations = r.IsDBNull(5) ? "None" : r.GetString(5)
            });
        }

        return Ok(list);
    }

    [HttpDelete("product/{id}")]
    public IActionResult DeleteProduct(int id)
    {
        int sellerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(
            "DELETE FROM dbo.product WHERE id=@id AND seller_id=@sid", con);

        cmd.Parameters.AddWithValue("@id", id);
        cmd.Parameters.AddWithValue("@sid", sellerId);

        cmd.ExecuteNonQuery();
        return Ok();
    }
    #endregion

    #region MANAGE ORDERS

    [HttpGet("orders/confirmed/customers")]
    public IActionResult GetConfirmedCustomers()
    {
        int sellerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(@"
        SELECT
            c.id AS customer_id,
            c.name AS customer_name,
            SUM(o.price * o.quantity) AS grand_total
        FROM dbo.orders o
        JOIN dbo.customer c ON c.id = o.customer_id
        WHERE o.seller_id = @sid AND o.status = 'Confirmed'
        GROUP BY c.id, c.name
        ORDER BY c.name
    ", con);

        cmd.Parameters.AddWithValue("@sid", sellerId);

        var list = new List<object>();
        using var r = cmd.ExecuteReader();
        while (r.Read())
        {
            list.Add(new
            {
                customerId = r.GetInt32(0),
                customerName = r.GetString(1),
                grandTotal = r.GetDecimal(2)
            });
        }

        return Ok(list);
    }

    [HttpGet("orders/confirmed/customer/{customerId}")]
    public IActionResult GetCustomerOrderDetails(int customerId)
    {
        int sellerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

        using var con = _db.GetConnection();
        con.Open();

        var cmd = new SqlCommand(@"
        SELECT
            p.name,
            o.price,
            o.quantity,
            (o.price * o.quantity) AS total,
            cr.info
        FROM dbo.orders o
        JOIN dbo.product p ON p.id = o.product_id
        LEFT JOIN dbo.cust_request cr ON cr.orders_id = o.id
        WHERE o.seller_id = @sid
          AND o.customer_id = @cid
          AND o.status = 'Confirmed'
        ORDER BY p.name
    ", con);

        cmd.Parameters.AddWithValue("@sid", sellerId);
        cmd.Parameters.AddWithValue("@cid", customerId);

        var rows = new List<object>();
        using var r = cmd.ExecuteReader();
        while (r.Read())
        {
            rows.Add(new
            {
                productName = r.GetString(0),
                price = r.GetDecimal(1),
                quantity = r.GetInt32(2),
                total = r.GetDecimal(3),
                customization = r.IsDBNull(4) ? "—" : r.GetString(4)
            });
        }

        return Ok(rows);
    }
    #endregion

}
