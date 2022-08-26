using Microsoft.EntityFrameworkCore.Migrations;

namespace Grimmuzzle.Service.Migrations
{
    public partial class rename_storage_to_pull : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn("InStorage", "FairyTales", "InPull");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn("InPull", "FairyTales", "InStorage");
        }
    }
}
