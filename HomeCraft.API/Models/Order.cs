public class Order
{
    public int Id { get; set; }
    public int ProductId { get; set; }
    public int CustomerId { get; set; }
    public int SellerId { get; set; }

    public string Name { get; set; }
    public decimal Price { get; set; }
    public int Quantity { get; set; }
    public string Customizations { get; set; }
    public string Status { get; set; }
}
