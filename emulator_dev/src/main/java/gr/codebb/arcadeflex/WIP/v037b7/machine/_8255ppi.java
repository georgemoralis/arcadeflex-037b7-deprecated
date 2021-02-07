/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.machine;

import static gr.codebb.arcadeflex.WIP.v037b7.machine._8255ppiH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

public class _8255ppi {

    static ppi8255_interface intf;

    /* local copy of the intf */

    public static class ppi8255 {

        int groupA_mode;
        int groupB_mode;
        /* input output status */
        int[] io = new int[4];
        /* data written to ports */
        int[] latch = new int[4];
        /* control */
        int control;
    }

    static ppi8255[] chips = new ppi8255[MAX_8255];

    public static void ppi8255_init(ppi8255_interface intfce) {
        int i;

        intf = intfce;
        /* keep a local pointer to the interface */


        for (i = 0; i < intf.num; i++) {
            chips[i] = new ppi8255();
            chips[i].groupA_mode = 0;
            /* group a mode */
            chips[i].groupB_mode = 0;
            /* group b mode */
            chips[i].io[0] = 0xff;
            /* all inputs */
            chips[i].io[1] = 0xff;
            /* all inputs */
            chips[i].io[2] = 0xff;
            /* all inputs */
            chips[i].latch[0] = 0x00;
            /* clear latch */
            chips[i].latch[1] = 0x00;
            /* clear latch */
            chips[i].latch[2] = 0x00;
            /* clear latch */
            chips[i].control = 0x1b;
        }
    }

    public static int ppi8255_r(int which, int offset) {
        ppi8255 chip;

        /* Some bounds checking */
        if (which > intf.num) {
            logerror("Attempting to read an unmapped 8255 chip\n");
            return 0;
        }

        if (offset > 3) {
            logerror("Attempting to read an invalid 8255 register\n");
            return 0;
        }

        chip = chips[which];

        switch (offset) {
            case 0:
                /* Port A read */
                if (chip.io[0] == 0) {
                    /* output */
                    return chip.latch[0];
                } else {
                    /* input */
                    if (intf.portA_r != null) {
                        return (intf.portA_r).handler(which);
                    }
                }
                break;

            case 1:
                /* Port B read */
                if (chip.io[1] == 0) {
                    /* output */
                    return chip.latch[1];
                } else {
                    /* input */
                    if (intf.portB_r != null) {
                        return (intf.portB_r).handler(which);
                    }
                }
                break;

            case 2: /* Port C read */ {
                int input = 0;

                /* read data */
                if (intf.portC_r != null) {
                    input = (intf.portC_r).handler(which);
                }

                /* return data - combination of input and latched output depending on
	                        the input/output status of each half of port C */
                return ((chip.latch[2] & ~chip.io[2]) | (input & chip.io[2]));
            }

            case 3:
                /* Control word */
                return 0x0ff;

            //return chip.control;
            //break;
        }

        logerror("8255 chip %d: Port %c is being read but has no handler", which, 'A' + offset);

        return 0x00;
    }

    public static void ppi8255_w(int which, int offset, int data) {
        ppi8255 chip;

        /* Some bounds checking */
        if (which > intf.num) {
            logerror("Attempting to write an unmapped 8255 chip\n");
            return;
        }

        if (offset > 3) {
            logerror("Attempting to write an invalid 8255 register\n");
            return;
        }

        chip = chips[which];

        /* store written data */
        chip.latch[offset] = data;

        switch (offset) {
            case 0: /* Port A write */ {
                int write_data;

                write_data = (chip.latch[0] & ~chip.io[0])
                        | (0x0ff & chip.io[0]);

                if (intf.portA_w != null) {
                    (intf.portA_w).handler(which, write_data);
                }
            }
            return;

            case 1: /* Port B write */ {
                int write_data;

                write_data = (chip.latch[1] & ~chip.io[1])
                        | (0x0ff & chip.io[1]);

                if (intf.portB_w != null) {
                    (intf.portB_w).handler(which, write_data);
                }
            }
            return;

            case 2: /* Port C write */ {
                int write_data;

                write_data = (chip.latch[2] & ~chip.io[2])
                        | (0x0ff & chip.io[2]);

                if (intf.portC_w != null) {
                    (intf.portC_w).handler(which, write_data);
                }
            }
            return;

            case 3:
                /* Control word */

                if ((data & 0x80) != 0) {
                    /* mode set */
                    chip.control = data;

                    chip.groupA_mode = (data >> 5) & 3;
                    chip.groupB_mode = (data >> 2) & 1;

                    if (chip.groupA_mode != 0 || chip.groupB_mode != 0) {
                        logerror("8255 chip %d: Setting an unsupported mode!\n", which);
                    }

                    /* Port A direction */
                    if ((data & 0x10) != 0) {
                        /* input */
                        chip.io[0] = 0xff;
                    } else {
                        /* output */
                        chip.io[0] = 0x00;
                    }

                    /* Port B direction */
                    if ((data & 0x02) != 0) {
                        chip.io[1] = 0xff;
                    } else {
                        chip.io[1] = 0x00;
                    }

                    /* Port C upper direction */
                    if ((data & 0x08) != 0) {
                        chip.io[2] |= 0xf0;
                    } else {
                        chip.io[2] &= 0x0f;
                    }

                    /* Port C lower direction */
                    if ((data & 0x01) != 0) {
                        chip.io[2] |= 0x0f;
                    } else {
                        chip.io[2] &= 0xf0;
                    }

                    /* KT: 25-Dec-99 - 8255 resets latches when mode set */
                    chip.latch[0] = chip.latch[1] = chip.latch[2] = 0;
                    {
                        int write_data;

                        write_data = (chip.latch[0] & ~chip.io[0])
                                | (0x0ff & chip.io[0]);

                        if (intf.portA_w != null) {
                            (intf.portA_w).handler(which, write_data);
                        }
                    }
                    {
                        int write_data;

                        write_data = (chip.latch[1] & ~chip.io[1])
                                | (0x0ff & chip.io[1]);

                        if (intf.portB_w != null) {
                            (intf.portB_w).handler(which, write_data);
                        }
                    }
                    {
                        int write_data;

                        write_data = (chip.latch[2] & ~chip.io[2])
                                | (0x0ff & chip.io[2]);

                        if (intf.portC_w != null) {
                            (intf.portC_w).handler(which, write_data);
                        }
                    }

                } else {
                    /* KT: 25-Dec-99 - Added port C bit set/reset feature */
 /* bit set/reset */
                    int bit;

                    bit = (data >> 1) & 0x07;

                    if ((data & 1) != 0) {
                        /* set bit */
                        chip.latch[2] |= (1 << bit);
                    } else {
                        /* bit reset */
                        chip.latch[2] &= ~(1 << bit);
                    }
                    {
                        int write_data;

                        write_data = (chip.latch[2] & ~chip.io[2])
                                | (0x0ff & chip.io[2]);

                        if (intf.portC_w != null) {
                            (intf.portC_w).handler(which, write_data);
                        }
                    }
                }
                return;
            //break;
        }

        logerror("8255 chip %d: Port %c is being written to but has no handler", which, 'A' + offset);
    }

    /* Helpers */
    public static ReadHandlerPtr ppi8255_0_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return ppi8255_r(0, offset);
        }
    };
    public static ReadHandlerPtr ppi8255_1_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return ppi8255_r(1, offset);
        }
    };
    public static ReadHandlerPtr ppi8255_2_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return ppi8255_r(2, offset);
        }
    };
    public static ReadHandlerPtr ppi8255_3_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return ppi8255_r(3, offset);
        }
    };
    public static WriteHandlerPtr ppi8255_0_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            ppi8255_w(0, offset, data);
        }
    };
    public static WriteHandlerPtr ppi8255_1_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            ppi8255_w(1, offset, data);
        }
    };
    public static WriteHandlerPtr ppi8255_2_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            ppi8255_w(2, offset, data);
        }
    };
    public static WriteHandlerPtr ppi8255_3_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            ppi8255_w(3, offset, data);
        }
    };
}
