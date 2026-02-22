using Microsoft.AspNetCore.Mvc;

namespace HomeCraft.API.Controllers;

[ApiController]
[Route("api/chatbot")]
public class ChatbotController : ControllerBase
{
    [HttpPost("query")]
    public IActionResult Query([FromBody] ChatQuery query)
    {
        string message = query.Message.ToLower();
        string response;

        if (message.Contains("price") || message.Contains("cost"))
            response = "Prices vary by artist and customization. Check the product page for details!";
        else if (message.Contains("delivery") || message.Contains("shipping"))
            response = "We ship globally! Standard shipping takes 5-7 business days.";
        else if (message.Contains("return") || message.Contains("refund"))
            response = "Returns are accepted within 7 days if the item is damaged.";
        else if (message.Contains("custom") || message.Contains("order"))
            response = "You can request customizations directly on the product's page!";
        else if (message.Contains("hello") || message.Contains("hi"))
            response = "Hello! Welcome to HomeCraft. How can I assist you today?";
        else
            response = "I'm not sure about that. Try asking about shipping, pricing, or customizations!";

        return Ok(new { response });
    }

    public class ChatQuery
    {
        public string Message { get; set; }
    }
}
