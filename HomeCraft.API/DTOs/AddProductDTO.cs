using Microsoft.AspNetCore.Http;

public class AddProductDTO
{
    public string? Name { get; set; }
    public string? Description { get; set; }
    public string? Category { get; set; }
    public string? Type { get; set; }
    public decimal? Price { get; set; }
    public string? Customizations { get; set; }
    public IFormFile? Image { get; set; }
}
