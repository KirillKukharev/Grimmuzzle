using System;
using Microsoft.EntityFrameworkCore.Migrations;

namespace Grimmuzzle.Service.Migrations
{
    public partial class change_store_flag_to_store_date : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "InStore",
                table: "FairyTales");

            migrationBuilder.AddColumn<DateTime>(
                name: "InStoreDate",
                table: "FairyTales",
                type: "datetime2",
                nullable: true);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "InStoreDate",
                table: "FairyTales");

            migrationBuilder.AddColumn<bool>(
                name: "InStore",
                table: "FairyTales",
                type: "bit",
                nullable: false,
                defaultValue: false);
        }
    }
}
