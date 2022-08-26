using Microsoft.EntityFrameworkCore.Migrations;

namespace Grimmuzzle.Service.Migrations
{
    public partial class add_in_storage_flag : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "InStorage",
                table: "FairyTales",
                type: "bit",
                nullable: false,
                defaultValue: false);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "InStorage",
                table: "FairyTales");
        }
    }
}
