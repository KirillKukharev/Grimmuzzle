using Microsoft.EntityFrameworkCore.Migrations;

namespace Grimmuzzle.Service.Migrations
{
    public partial class AddParameters : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "SerializedParameters",
                table: "FairyTales",
                type: "nvarchar(max)",
                nullable: true);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "SerializedParameters",
                table: "FairyTales");
        }
    }
}
