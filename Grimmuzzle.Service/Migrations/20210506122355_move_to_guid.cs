using System;
using Microsoft.EntityFrameworkCore.Migrations;

namespace Grimmuzzle.Service.Migrations
{
    public partial class move_to_guid : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {

            migrationBuilder.DropPrimaryKey("PK_FairyTales", "FairyTales");
            migrationBuilder.DropColumn("Id", "FairyTales");
            migrationBuilder.AddColumn<Guid>
                (name: "Id", 
                 table: "FairyTales", 
                 type: "uniqueidentifier", 
                 nullable: false,
                 defaultValueSql: "NewId()");
            migrationBuilder.AddPrimaryKey("PK_FairyTales", "FairyTales", "Id");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropPrimaryKey("PK_FairyTales", "FairyTales");
            migrationBuilder.DropColumn("Id", "FairyTales");
            migrationBuilder.AddColumn<long>
                (name: "Id",
                 table: "FairyTales",
                 type: "bigint",
                 nullable: false,
                 defaultValueSql: "NewId()");
            migrationBuilder.AddPrimaryKey("PK_FairyTales", "FairyTales", "Id");

        }
    }
}
