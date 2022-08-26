using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using System;
using System.IO;
using System.Reflection;
using Grimmuzzle.Service.Models;
using Microsoft.EntityFrameworkCore;
using Serilog;
using Serilog.Events;

namespace Grimmuzzle.Service
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            AppUtils.Configuration = configuration;
        }

        /// <summary>
        /// This method gets called by the runtime. Use this method to add services to the container.
        /// </summary>
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddControllers();
            services.SetupFairyTaleDi()
                .AddSwaggerGen(op =>
                {
                    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
                    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);

                    op.IncludeXmlComments(xmlPath);
                })
                .AddCors();
        }

        /// <summary>
        /// This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        /// </summary>
        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsStaging() || env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            using (var serviceScope = app.ApplicationServices.CreateScope())
            {
                var context = serviceScope.ServiceProvider.GetService<GrimmuzzleContext>();
                context.Database.Migrate();
            }

            Log.Logger = new LoggerConfiguration()
                .MinimumLevel.Debug()
                .WriteTo.File(Path.Combine(env.ContentRootPath, "logs", "all-.log"), rollingInterval: RollingInterval.Day)
                .WriteTo.File(Path.Combine(env.ContentRootPath, "logs", "error-.log"), LogEventLevel.Error, rollingInterval: RollingInterval.Day)
                .CreateLogger();

            app.SetupSwagger()
                .UseHttpsRedirection()
                .UseRouting()
                .ChooseBuilderWithCors(env)
                .UseAuthorization()
                .UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });
        }
    }

}