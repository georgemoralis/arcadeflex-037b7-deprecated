/** ported to 0.37b7 */
package gr.codebb.arcadeflex.WIP.v037b7.mame;

import static gr.codebb.arcadeflex.common.PtrLib.*;

public class ui_textH {

    /* Important: this must match the default_text list in ui_text.c! */
    public static final int UI_first_entry = -1;

    public static final int UI_mame = 0;

    /* copyright stuff */
    public static final int UI_copyright1 = 1;
    public static final int UI_copyright2 = 2;
    public static final int UI_copyright3 = 3;

    /* misc menu stuff */
    public static final int UI_returntomain = 4;
    public static final int UI_returntoprior = 5;
    public static final int UI_anykey = 6;
    public static final int UI_on = 7;
    public static final int UI_off = 8;
    public static final int UI_NA = 9;
    public static final int UI_INVALID = 10;
    public static final int UI_none = 11;
    public static final int UI_cpu = 12;
    public static final int UI_address = 13;
    public static final int UI_value = 14;
    public static final int UI_sound = 15;
    public static final int UI_sound_lc = 16;
    /* lower-case version */
    public static final int UI_stereo = 17;
    public static final int UI_vectorgame = 18;
    public static final int UI_screenres = 19;
    public static final int UI_text = 20;
    public static final int UI_volume = 21;
    public static final int UI_relative = 22;
    public static final int UI_allchannels = 23;
    public static final int UI_brightness = 24;
    public static final int UI_gamma = 25;
    public static final int UI_vectorintensity = 26;
    public static final int UI_overclock = 27;
    public static final int UI_allcpus = 28;
    public static final int UI_historymissing = 29;

    /* special characters */
    public static final int UI_leftarrow = 30;
    public static final int UI_rightarrow = 31;
    public static final int UI_uparrow = 32;
    public static final int UI_downarrow = 33;
    public static final int UI_lefthilight = 34;
    public static final int UI_righthilight = 35;

    /* warnings */
    public static final int UI_knownproblems = 36;
    public static final int UI_imperfectcolors = 37;
    public static final int UI_wrongcolors = 38;
    public static final int UI_imperfectsound = 39;
    public static final int UI_nosound = 40;
    public static final int UI_nococktail = 41;
    public static final int UI_brokengame = 42;
    public static final int UI_brokenprotection = 43;
    public static final int UI_workingclones = 44;
    public static final int UI_typeok = 45;

    /* main menu */
    public static final int UI_inputgeneral = 46;
    public static final int UI_dipswitches = 47;
    public static final int UI_analogcontrols = 48;
    public static final int UI_calibrate = 49;
    public static final int UI_bookkeeping = 50;
    public static final int UI_inputspecific = 51;
    public static final int UI_gameinfo = 52;
    public static final int UI_history = 53;
    public static final int UI_resetgame = 54;
    public static final int UI_returntogame = 55;

    public static final int UI_cheat = 56;
    public static final int UI_memorycard = 57;

    /* input stuff */
    public static final int UI_keyjoyspeed = 58;
    public static final int UI_reverse = 59;
    public static final int UI_sensitivity = 60;

    /* stats */
    public static final int UI_tickets = 61;
    public static final int UI_coin = 62;
    public static final int UI_locked = 63;

    /* memory card */
    public static final int UI_loadcard = 64;
    public static final int UI_ejectcard = 65;
    public static final int UI_createcard = 66;
    public static final int UI_resetcard = 67;
    public static final int UI_loadfailed = 68;
    public static final int UI_loadok = 69;
    public static final int UI_cardejected = 70;
    public static final int UI_cardcreated = 71;
    public static final int UI_cardcreatedfailed = 72;
    public static final int UI_cardcreatedfailed2 = 73;
    public static final int UI_carderror = 74;

    /* cheat stuff */
    public static final int UI_enablecheat = 75;
    public static final int UI_addeditcheat = 76;
    public static final int UI_startcheat = 77;
    public static final int UI_continuesearch = 78;
    public static final int UI_viewresults = 79;
    public static final int UI_restoreresults = 80;
    public static final int UI_memorywatch = 81;
    public static final int UI_generalhelp = 82;
    public static final int UI_watchpoint = 83;
    public static final int UI_disabled = 84;
    public static final int UI_cheats = 85;
    public static final int UI_watchpoints = 86;
    public static final int UI_moreinfo = 87;
    public static final int UI_moreinfoheader = 88;
    public static final int UI_cheatname = 89;
    public static final int UI_cheatdescription = 90;
    public static final int UI_code = 91;

    /* watchpoint stuff */
    public static final int UI_watchlength = 92;
    public static final int UI_watchlabeltype = 93;
    public static final int UI_watchlabel = 94;
    public static final int UI_watchx = 95;
    public static final int UI_watchy = 96;
    public static final int UI_watch = 97;

    /* search stuff */
    public static final int UI_search_lives = 98;
    public static final int UI_search_timers = 99;
    public static final int UI_search_energy = 100;
    public static final int UI_search_status = 101;
    public static final int UI_search_slow = 102;
    public static final int UI_search_speed = 103;
    public static final int UI_search_matches_found = 104;
    public static final int UI_search_noinit = 105;
    public static final int UI_search_nosave = 106;
    public static final int UI_search_done = 107;
    public static final int UI_search_OK = 108;

    public static final int UI_last_entry = 109;

    public static class lang_struct {

        public int version;
        public int multibyte;/* UNUSED: 1 if this is a multibyte font/language */
        public UBytePtr fontdata;/* pointer to the raw font data to be decoded */
        public char fontglyphs;/* total number of glyps in the external font - 1 */
        public String langname;
        public String fontname;
        public String author;
    }
}
