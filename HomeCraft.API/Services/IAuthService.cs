public interface IAuthService
{
    AuthResult Login(string email, string password);
}
