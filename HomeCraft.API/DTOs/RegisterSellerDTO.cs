public class RegisterSellerDTO
{
    public string Name { get; set; }
    public string Location { get; set; }
    public string Craft { get; set; }
    public string Email { get; set; }
    public string Password { get; set; }

    public string Phone { get; set; }
    public string LicenseNumber { get; set; }

    public IFormFile ProfileImage { get; set; }
    public IFormFile AadharImage { get; set; }
}
