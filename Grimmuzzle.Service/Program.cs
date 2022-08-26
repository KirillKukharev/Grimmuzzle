using System.IO;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;

namespace Grimmuzzle.Service
{
    public class Program
    {
        public static void Main(string[] args)
        {
            CreateHostBuilder(args).Build().Run();
        }

        public static IHostBuilder CreateHostBuilder(string[] args) =>
            Host.CreateDefaultBuilder(args)
                .ConfigureAppConfiguration((context, builder) =>
                {
                    var env = context.HostingEnvironment;

                    builder
                        .AddJsonFile(Path.Combine(Directory.GetCurrentDirectory(), "appsettings.json"))
                        .AddJsonFile(Path.Combine(Directory.GetCurrentDirectory(), $"appsettings.{env.EnvironmentName}.json"))
                        .AddJsonFile(Path.Combine(Directory.GetCurrentDirectory(), "fairyTaleConstructor.json"), false, false);
                })
                .ConfigureWebHostDefaults(webBuilder =>
                {
                    webBuilder.UseStartup<Startup>();
                });
    }
}
