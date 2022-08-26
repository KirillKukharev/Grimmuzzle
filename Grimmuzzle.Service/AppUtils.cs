using Grimmuzzle.Service.Interfaces;
using Grimmuzzle.Service.Models;
using Grimmuzzle.Service.Repositories;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using System;
using System.IO;
using Microsoft.IdentityModel.Protocols;
using Grimmuzzle.Service.Queue;

namespace Grimmuzzle.Service
{
    public static class AppUtils
    {
        public static IConfiguration Configuration { get; set; }

        public static IApplicationBuilder SetupSwagger(this IApplicationBuilder app)
        {
            app
                .UseSwagger()
                .UseSwaggerUI(o => { o.SwaggerEndpoint("/swagger/v1/swagger.json", "GrimmuzzleAPI"); });
            return app;
        }

        public static IApplicationBuilder ChooseBuilderWithCors(this IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseCors(builder => { builder.AllowAnyOrigin(); });
            }
            else
            {
                app.UseCors(builder =>
                {
                    builder.WithOrigins(Configuration.GetValue<string>("applicationUrl", ""),
                        Configuration.GetValue<string>("ClientsUrl", ""));
                });
            }
            return app;
        }

        public static IServiceCollection SetupFairyTaleDi(this IServiceCollection services)
        {
            services.AddHttpClient("ML", c =>
            {
                c.BaseAddress = new Uri(Configuration["MLService:EN_WebServiceURL"]);
            });
            services.AddHttpClient("MLRU", c =>
            {
                c.BaseAddress = new Uri(Configuration["MLService:RU_WebServiceURL"]);
            });
            services
                .Configure<FairyTaleGeneratorConfiguration>(Configuration.GetSection("MLService"))
                .AddScoped<IFairyTaleGeneratorService, FairyTaleGeneratorWebRequest>()
                .Configure<RequestsQueueConnectionConfiguration>(Configuration.GetSection("ServiceBusQeueues"))
                .AddSingleton<IRequestsQueue, RequestsQueue>()
                .AddDbContext<GrimmuzzleContext>(options =>
                    options.UseSqlServer(Configuration.GetConnectionString("DefaultConnection")))
                .AddScoped<IAsyncFairyTaleRepository, FairyTaleRepository>()
                .AddSingleton<IAsyncConstructorParamsRepository, ConstructorParamsRepository>();
            services.AddHostedService<QueueHostedService>();
            services.AddHostedService<DeadLetterQueueHostedService>();
            return services;
        }
    }
}
