using Microsoft.EntityFrameworkCore;

namespace Grimmuzzle.Service.Models
{
    /// <summary>
    /// Data access layer which interacts with database using DbContext.
    /// </summary>
    public class GrimmuzzleContext : DbContext
    {
        public GrimmuzzleContext(DbContextOptions<GrimmuzzleContext> options) : base(options)
        { }

        public GrimmuzzleContext() { }
        /// <summary>
        /// Access to FairyTales table into database.
        /// </summary>
        public virtual DbSet<FairyTale> FairyTales { get; set; }
    }
}