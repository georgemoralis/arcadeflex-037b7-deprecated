/*
 * ported to 0.37b7 
*/
package gr.codebb.arcadeflex.WIP.v037b7.mame;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.ui_textH.*;

public class ui_text {

    public static lang_struct lang = new lang_struct();

    public static String[] trans_text = new String[UI_last_entry + 1];

    /* All entries in this table must match the enum ordering in "ui_text.h" */
    public static String default_text[]
            = {
                "MAME",
                /* copyright stuff */
                "Usage of emulators in conjunction with ROMs you don't own is forbidden by copyright law.",
                "IF YOU ARE NOT LEGALLY ENTITLED TO PLAY \"%s\" ON THIS EMULATOR, PRESS ESC.",
                "Otherwise, type OK to continue",
                /* misc stuff */
                "Return to Main Menu",
                "Return to Prior Menu",
                "Press Any Key",
                "On",
                "Off",
                "NA",
                "INVALID",
                "(none)",
                "CPU",
                "Address",
                "Value",
                "Sound",
                "sound",
                "stereo",
                "Vector Game",
                "Screen Resolution",
                "Text",
                "Volume",
                "Relative",
                "ALL CHANNELS",
                "Brightness",
                "Gamma",
                "Vector Intensity",
                "Overclock",
                "ALL CPUS",
                "History not available",
                /* special characters */
                "\u0011",
                "\u0010",
                "\u0018",
                "\u0019",
                "\u001A",
                "\u001b",
                /* known problems */
                "There are known problems with this game:",
                "The colors aren't 100% accurate.",
                "The colors are completely wrong.",
                "The sound emulation isn't 100% accurate.",
                "The game lacks sound.",
                "Screen flipping in cocktail mode is not supported.",
                "THIS GAME DOESN'T WORK PROPERLY",
                "The game has protection which isn't fully emulated.",
                "There are working clones of this game. They are:",
                "Type OK to continue",
                /* main menu */
                "Input (general)",
                "Dip Switches",
                "Analog Controls",
                "Calibrate Joysticks",
                "Bookkeeping Info",
                "Input (this game)",
                "Game Information",
                "Game History",
                "Reset Game",
                "Return to Game",
                "Cheat",
                "Memory Card",
                /* input */
                "Key/Joy Speed",
                "Reverse",
                "Sensitivity",
                /* stats */
                "Tickets dispensed",
                "Coin",
                "(locked)",
                /* memory card */
                "Load Memory Card",
                "Eject Memory Card",
                "Create Memory Card",
                "Call Memory Card Manager (RESET)",
                "Failed To Load Memory Card!",
                "Load OK!",
                "Memory Card Ejected!",
                "Memory Card Created OK!",
                "Failed To Create Memory Card!",
                "(It already exists ?)",
                "DAMN!! Internal Error!",
                /* cheats */
                "Enable/Disable a Cheat",
                "Add/Edit a Cheat",
                "Start a New Cheat Search",
                "Continue Search",
                "View Last Results",
                "Restore Previous Results",
                "Configure Watchpoints",
                "General Help",
                "Watchpoint",
                "Disabled",
                "Cheats",
                "Watchpoints",
                "More Info",
                "More Info for",
                "Name",
                "Description",
                "Code",
                /* watchpoints */
                "Number of bytes",
                "Label type",
                "Label",
                "X position",
                "Y position",
                "Watch",
                /* searching */
                "Lives (or some other value)",
                "Timers (+/- some value)",
                "Energy (greater or less)",
                "Status (bits or flags)",
                "Slow but sure (changed or not)",
                "Change Search Speed",
                "Matches found",
                "Search not initialized",
                "No previous values saved",
                "Previous values already restored",
                "Restoration successful",
                null
            };

    public static int uistring_init(Object langfile) {
        /*
        TODO: This routine needs to do several things:
			- load an external font if needed
			- determine the number of characters in the font
			- deal with multibyte languages

         */

        int i;
        /*TODO*///	char curline[255];
/*TODO*///	char section[255] = "\0";
/*TODO*///	char *ptr;
/*TODO*///
        /* Clear out any default strings */
        for (i = 0; i <= UI_last_entry; i++) {
            trans_text[i] = null;
        }

        lang = new lang_struct();//memset(&lang, 0, sizeof(lang));

        if (langfile == null) {
            return 0;
        }

        throw new UnsupportedOperationException("unsupported");
        /*TODO*///	while (osd_fgets (curline, 255, langfile) != NULL)
/*TODO*///	{
/*TODO*///		/* Ignore commented and blank lines */
/*TODO*///		if (curline[0] == ';') continue;
/*TODO*///		if (curline[0] == '\n') continue;
/*TODO*///
/*TODO*///		if (curline[0] == '[')
/*TODO*///		{
/*TODO*///			ptr = strtok (&curline[1], "]");
/*TODO*///			/* Found a section, indicate as such */
/*TODO*///			strcpy (section, ptr);
/*TODO*///
/*TODO*///			/* Skip to the next line */
/*TODO*///			continue;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* Parse the LangInfo section */
/*TODO*///		if (strcmp (section, "LangInfo") == 0)
/*TODO*///		{
/*TODO*///			ptr = strtok (curline, "=");
/*TODO*///			if (strcmp (ptr, "Version") == 0)
/*TODO*///			{
/*TODO*///				ptr = strtok (NULL, "\n");
/*TODO*///				sscanf (ptr, "%d", &lang.version);
/*TODO*///			}
/*TODO*///			else if (strcmp (ptr, "Language") == 0)
/*TODO*///			{
/*TODO*///				ptr = strtok (NULL, "\n");
/*TODO*///				strcpy (lang.langname, ptr);
/*TODO*///			}
/*TODO*///			else if (strcmp (ptr, "Author") == 0)
/*TODO*///			{
/*TODO*///				ptr = strtok (NULL, "\n");
/*TODO*///				strcpy (lang.author, ptr);
/*TODO*///			}
/*TODO*///			else if (strcmp (ptr, "Font") == 0)
/*TODO*///			{
/*TODO*///				ptr = strtok (NULL, "\n");
/*TODO*///				strcpy (lang.fontname, ptr);
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* Parse the Strings section */
/*TODO*///		if (strcmp (section, "Strings") == 0)
/*TODO*///		{
/*TODO*///			/* Get all text up to the first line ending */
/*TODO*///			ptr = strtok (curline, "\n");
/*TODO*///
/*TODO*///			/* Find a matching default string */
/*TODO*///			for (i = 0; i < UI_last_entry; i ++)
/*TODO*///			{
/*TODO*///				if (strcmp (curline, default_text[i]) == 0)
/*TODO*///				{
/*TODO*///					char transline[255];
/*TODO*///
/*TODO*///					/* Found a match, read next line as the translation */
/*TODO*///					osd_fgets (transline, 255, langfile);
/*TODO*///
/*TODO*///					/* Get all text up to the first line ending */
/*TODO*///					ptr = strtok (transline, "\n");
/*TODO*///
/*TODO*///					/* Allocate storage and copy the string */
/*TODO*///					trans_text[i] = malloc (strlen(transline)+1);
/*TODO*///					strcpy (trans_text[i], transline);
/*TODO*///
/*TODO*///					/* Done searching */
/*TODO*///					break;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* indicate success */
/*TODO*///        return 0;
    }

    public static void uistring_shutdown() {
        int i;

        /* Deallocate storage for the strings */
        for (i = 0; i <= UI_last_entry; i++) {
            if (trans_text[i] != null) {
                trans_text[i] = null;
            }
        }
    }

    public static String ui_getstring(int string_num) {
        /* Try to use the language file strings first */
        if (trans_text[string_num] != null) {
            return trans_text[string_num];
        } else /* That failed, use the default strings */ {
            return default_text[string_num];
        }
    }
}
