public class Workshop
{
    public int Id { get; set; }
    public int SellerId { get; set; }
    public string Topic { get; set; }
    public string Venue { get; set; }
    public DateTime Timing { get; set; }
    public int Capacity { get; set; }
    public bool IsPaid { get; set; }
}
