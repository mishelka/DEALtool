/**
 * The support for the replay function.
 * Not used yet.
 */
@Parser(
    className = "gui.tools.ProgramLanguageParser",
    mainNode = "gui.model.application.Program",
    tokens = {
        @TokenDef(name = "EQ", regexp = "\"=\""),
        @TokenDef(name = "COMMA", regexp = "\",\""),
        @TokenDef(name = "LPAR", regexp = "\"(\""),
        @TokenDef(name = "RPAR", regexp = "\")\""),
//        @TokenDef(name = "NAME", regexp = "((~[\" \",\"\\t\",\"\\n\",\"\\r\"])+)|(\"[\"(~[\"]\"])+\"]\")"),
@TokenDef(name = "NAME", regexp = "((~[\"\\\"\",\"=\",\"(\",\")\",\" \",\"\\t\",\"\\n\",\"\\r\"])+)|(\"[\"(~[\"]\"])+\"]\")"),
        
@TokenDef(name = "VALUE", regexp = "\"\\\"\"(~[\"\\\"\"])*\"\\\"\"")
    },
    skips = {
        @Skip("\" \""), 
        @Skip("\"\\t\""), 
        @Skip("\"\\n\""), 
        @Skip("\"\\r\"")
    }
)
package gui.model.application.program;

import yajco.annotation.config.Parser;
import yajco.annotation.config.Skip;
import yajco.annotation.config.TokenDef;

