public class RegisterCustomerDTO
{
    public string Name { get; set; }
    public string Location { get; set; }
    public string Interests { get; set; }
    public string Email { get; set; }
    public string Password { get; set; }

    public string Phone { get; set; }
    public IFormFile ProfileImage { get; set; }
}
