/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.sndhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.sndhrdw.gorf.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.common.libc.expressions.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.samples.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.v037b7.cpu.z80.z80H.Z80_BC;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.input_port_2_r;

import gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.GameSamples;
import gr.codebb.arcadeflex.v037b7.mame.sndintrfH.MachineSound;

public class gorf {

    public static int GorfBaseFrequency;
    /* Some games (Qbert) change this */
    public static int GorfBaseVolume;
    public static int GorfChannel = 0;
    public GameSamples GorfSamples;

    /**
     * **************************************************************************
     * 64 Phonemes - currently 1 sample per phoneme, will be combined sometime!
     * **************************************************************************
     */
    static String PhonemeTable[]
            = {
                "EH3", "EH2", "EH1", "PA0", "DT", "A1", "A2", "ZH",
                "AH2", "I3", "I2", "I1", "M", "N", "B", "V",
                "CH", "SH", "Z", "AW1", "NG", "AH1", "OO1", "OO",
                "L", "K", "J", "H", "G", "F", "D", "S",
                "A", "AY", "Y1", "UH3", "AH", "P", "O", "I",
                "U", "Y", "T", "R", "E", "W", "AE", "AE1",
                "AW2", "UH2", "UH1", "UH", "O2", "O1", "IU", "U1",
                "THV", "TH", "ER", "EH", "E1", "AW", "PA1", "STOP",
                null
            };

    static String GorfWordTable[]
            = {
                "A2AYY1", "A2E1", "UH1GEH1I3N", "AE1EH2M", "AEM",
                "AE1EH3ND", "UH1NAH2I1YLA2SHUH2N", "AH2NUHTHER", "AH1NUHTHVRR",
                "AH1R", "UHR", "UH1VEH1EH3NNDJER", "BAEEH3D", "BAEEH1D", "BE",
                "BEH3EH1N", "buht", "BUH1DTTEH2NN", "KUHDEH2T",
                "KAE1NUH1T", "KAE1EH3PTI3N",
                "KRAH2UH3NI3KUH3O2LZ", "KO1UH3I3E1N", "KO1UH3I3E1NS",
                "KERNAH2L", "KAH1NCHEHSNEHS", "DE1FEH1NDER",
                "DE1STRO1I1Y", "DE1STRO1I1Y1D",
                "DU1UM", "DRAW1S", "EHMPAH2I3YR", "EHND",
                "EH1NEH1MY", "EH1SKA1E1P", "FLEHGSHIP",
                "FOR", "GUH1LAEKTI1K",
                "DJEH2NERUH3L", "GDTO1O1RRFF", "GDTO1RFYA2N", "GDTO1RFE1EH2N", "GDTO1RFYA2NS",
                "HAH1HAH1HAH1HAH1", "hahaher.wav", "HUHRDER",
                "HAE1EH3V", "HI1TI1NG", "AH1I1Y", "AH1I1Y1", "I1MPAH1SI1BL",
                "IN*", "INSERT", "I1S", "LI1V", "LAWNG", "MEE1T", "MUU1V",
                "MAH2I1Y", "MAH2I3Y", "NIR", "NEHKST", "NUH3AH2YS", "NO",
                "NAH1O1U1W", "PA1", "PLA1AYER", "PRE1PAE1ER", "PRI1SI3NEH3RS",
                "PRUH2MOTEH3D", "POO1IUSH", "RO1U1BAH1T", "RO1U1BAH1TS",
                "RO1U1BAH1UH3TS", "SEK", "SHIP", "SHAH1UH3T", "SUHM", "SPA2I3YS", "PA0",
                "SERVAH2I1Y1VUH3L", "TAK", "THVUH", "THVUH1",
                "THUH", "TAH1EH3YM", "TU", "TIUU1",
                "UH2NBE1AYTUH3BUH3L",
                "WORAYY1EH3R", "WORAYY1EH3RS", "WI1L",
                "Y1I3U1", "YIUU1U1", "YI1U1U1", "Y1IUU1U1", "Y1I1U1U1", "YOR", "YU1O1RSEH1LF", "s.wav",
                "FO1R", "FO2R", "WIL", "GDTO1RVYA2N",
                "KO1UH3I3AYNN",
                "UH1TAEEH3K", "BAH2I3Y1T", "KAH1NKER", "DYVAH1U1ER", "DUHST", "GAE1LUH1KSY", "GAH1EH3T",
                "PAH1I1R", "TRAH2I1Y", "SU1PRE1N", "AWL", "HA2AYL",
                "EH1MPAH1I1R",
                null
            };

    public static final int num_samples = GorfWordTable.length;//(sizeof(GorfWordTable)/sizeof(char *))

    public static String gorf_sample_names[]
            = {
                "*gorf", "a.wav", "a.wav", "again.wav", "am.wav", "am.wav", "and.wav", "anhilatn.wav",
                "another.wav", "another.wav", "are.wav", "are.wav",
                "avenger.wav", "bad.wav", "bad.wav", "be.wav",
                "been.wav", "but.wav", "button.wav", "cadet.wav",
                "cannot.wav", "captain.wav", "chronicl.wav", "coin.wav", "coins.wav", "colonel.wav",
                "consciou.wav", "defender.wav", "destroy.wav", "destroyd.wav",
                "doom.wav", "draws.wav", "empire.wav", "end.wav",
                "enemy.wav", "escape.wav", "flagship.wav", "for.wav", "galactic.wav",
                "general.wav", "gorf.wav", "gorphian.wav", "gorphian.wav", "gorphins.wav",
                "hahahahu.wav", "hahaher.wav", "harder.wav", "have.wav",
                "hitting.wav", "i.wav", "i.wav", "impossib.wav", "in.wav", "insert.wav",
                "is.wav", "live.wav", "long.wav", "meet.wav", "move.wav",
                "my.wav", "my.wav",
                "near.wav", "next.wav", "nice.wav", "no.wav",
                "now.wav", "pause.wav", "player.wav", "prepare.wav", "prisonrs.wav",
                "promoted.wav", "push.wav", "robot.wav", "robots.wav", "robots.wav",
                "seek.wav", "ship.wav", "shot.wav", "some.wav", "space.wav", "spause.wav",
                "survival.wav", "take.wav", "the.wav", "the.wav", "the.wav", "time.wav",
                "to.wav", "to.wav", "unbeatab.wav",
                "warrior.wav", "warriors.wav", "will.wav",
                "you.wav", "you.wav", "you.wav", "you.wav", "your.wav", "your.wav", "yourself.wav",
                "s.wav", "for.wav", "for.wav", "will.wav", "gorph.wav",
                // Missing Samples
                "coin.wav", "attack.wav", "bite.wav", "conquer.wav", "devour.wav", "dust.wav",
                "galaxy.wav", "got.wav", "power.wav", "try.wav", "supreme.wav", "all.wav",
                "hail.wav", "emperor.wav",
                null
            };

    /* Total word to join the phonemes together - Global to make it easier to use */
    //char totalword[256], *totalword_ptr;
    //char oldword[256];
    public static String totalword;
    public static String oldword;
    public static int plural = 0;

    public static ShStartPtr gorf_sh_start = new ShStartPtr() {
        public int handler(MachineSound msound) {
            GorfBaseFrequency = 11025;
            GorfBaseVolume = 230;
            GorfChannel = 0;
            return 0;
        }
    };

    public static ReadHandlerPtr gorf_speech_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int Phoneme, Intonation;
            int i = 0;

            int data;

            //totalword_ptr = totalword;
            data = cpu_get_reg(Z80_BC) >> 8;

            Phoneme = data & 0x3F;
            Intonation = data >> 6;

            logerror("Date : %d Speech : %s at intonation %d\n", Phoneme, PhonemeTable[Phoneme], Intonation);

            if (Phoneme == 63) {
                sample_stop(GorfChannel);
                if (strlen(totalword) > 2) {
                    logerror("Clearing sample %s\n", totalword);
                }
                totalword = null;
                /* Clear the total word stack */
                return data;
            }

            /* Phoneme to word translation */
            if (totalword == null) {
                totalword = PhonemeTable[Phoneme];
                /* Copy over the first phoneme */
                if (plural != 0) {
                    logerror("found a possible plural at %d\n", plural - 1);
                    if (totalword.equals("S")) {
                        /* Plural check */
                        sample_start(GorfChannel, num_samples - 2, 0);
                        /* play the sample at position of word */
                        sample_set_freq(GorfChannel, GorfBaseFrequency);
                        /* play at correct rate */
                        totalword = null;
                        /* Clear the total word stack */
                        oldword = null;
                        /* Clear the total word stack */
                        return data;
                    } else {
                        plural = 0;
                    }
                }
            } else {
                totalword += PhonemeTable[Phoneme];
                /* Copy over the first phoneme */
            }

            logerror("Total word = %s\n", totalword);

            for (i = 0; GorfWordTable[i] != null; i++) {
                if (totalword.equals(GorfWordTable[i])) {
                    /* Scan the word (sample) table for the complete word */
                    if ((totalword.equals("GDTO1RFYA2N")) || (totalword.equals("RO1U1BAH1T")) || (totalword.equals("KO1UH3I3E1N")) || (totalword.equals("WORAYY1EH3R")) || (totalword.equals("IN"))) {
                        /* May be plural */
                        plural = i + 1;
                        oldword = totalword;
                        logerror("Storing sample position %d and copying string %s\n", plural, oldword);
                    } else {
                        plural = 0;
                    }
                    sample_start(GorfChannel, i, 0);
                    /* play the sample at position of word */
                    sample_set_freq(GorfChannel, GorfBaseFrequency);
                    /* play at correct rate */
                    logerror("Playing sample %d", i);
                    totalword = null;
                    /* Clear the total word stack */
                    return data;
                }
            }

            /* Note : We should really also use volume in this as well as frequency */
            return data;
            /* Return nicely */
        }
    };

    static int gorf_status_r() {
        return NOT(sample_playing(GorfChannel));
    }

    /* Read from port 2 (0x12) returns speech status as 0x80 */
    public static ReadHandlerPtr gorf_port_2_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int Ans;

            Ans = (input_port_2_r.handler(0) & 0x7F);
            if (gorf_status_r() != 0) {
                Ans += 128;
            }
            return Ans;
        }
    };

    public static ShUpdatePtr gorf_sh_update = new ShUpdatePtr() {
        public void handler() {
        }
    };
}
