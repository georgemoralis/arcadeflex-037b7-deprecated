package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

public class tms34061H {
    /****************************************************************************
     *																			*
     *	Function prototypes and constants used by the TMS34061 emulator			*
     *																			*
     *  Created by Zsolt Vasvari on 5/26/1998.	                                *
     *																			*
     ****************************************************************************/

    /* Callback prototypes */

    /* Return the function code (FS0-FS2) selected by this offset */
    //typedef int  (*TMS34061_getfunction_t)  (int offset);
    public abstract static interface TMS34061_getfunction_t {
        public abstract int handler(int offset);
    }

    /* Return the row address (RA0-RA8) selected by this offset */
    //typedef int  (*TMS34061_getrowaddress_t)(int offset);
    public abstract static interface TMS34061_getrowaddress_t {
        public abstract int handler(int offset);
    }

    /* Return the column address (CA0-CA8) selected by this offset */
    //typedef int  (*TMS34061_getcoladdress_t)(int offset);
    public abstract static interface TMS34061_getcoladdress_t {
        public abstract int handler(int offset);
    }

    /* Function called to get a pixel */
    //typedef int  (*TMS34061_getpixel_t)(int col, int row);
    public abstract static interface TMS34061_getpixel_t {
        public abstract int handler(int col, int row);
    }

    /* Function called to set a pixel */
    //typedef void (*TMS34061_setpixel_t)(int col, int row, int pixel);
    public abstract static interface TMS34061_setpixel_t {
        public abstract int handler(int col, int row, int pixel);
    }


    public static class TMS34061interface
    {
            //int reg_addr_mode;               /* One of the addressing mode constants above */
        public TMS34061_getfunction_t   getfunction;
        public TMS34061_getrowaddress_t getrowaddress;
        public TMS34061_getcoladdress_t getcoladdress;
        public TMS34061_getpixel_t      getpixel;
        public TMS34061_setpixel_t      setpixel;
        public int cpu;                         /* Which CPU is the TMS34061 causing interrupts on */
        public InterruptPtr vertical_interrupt; /* Function called on a vertical interrupt */
        
        public TMS34061interface(
            TMS34061_getfunction_t   getfunction,
            TMS34061_getrowaddress_t getrowaddress,
            TMS34061_getcoladdress_t getcoladdress,
            TMS34061_getpixel_t      getpixel,
            TMS34061_setpixel_t      setpixel,
            int cpu,
            InterruptPtr vertical_interrupt) 
        {
            this.getfunction = getfunction;
            this.getrowaddress = getrowaddress;
            this.getcoladdress = getcoladdress;
            this.getpixel = getpixel;
            this.setpixel = setpixel;
            this.cpu = cpu;
            this.vertical_interrupt = vertical_interrupt;
        }
    };


    /* Initializes the emulator */
    //int TMS34061_start(TMS34061interface interface);

    /* Cleans up the emulation */

    /* Writes to the 34061 */

    /* Reads from the 34061 */

    /* Checks whether the display is inhibited */

    
}
