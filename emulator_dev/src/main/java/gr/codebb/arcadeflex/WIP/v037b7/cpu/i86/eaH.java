/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.I86H.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86.*;

public class eaH {
    public static /*unsigned*/ int EA;
    public static /*unsigned*/ int EO; /* HJB 12/13/98 effective offset of the address (before segment is added) */


    static GetEAPtr EA_000 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0]-=7; 
            EO=(I.regs.w[BX]+I.regs.w[SI])&0xFFFF; 
            EA=DefaultBase(DS)+EO; 
            return EA;
        }
    };
    static GetEAPtr EA_001 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 8;
            EO = (I.regs.w[BX] + I.regs.w[DI]) & 0xFFFF;
            EA = DefaultBase(DS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_002 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 8;
            EO = (I.regs.w[BP] + I.regs.w[SI]) & 0xFFFF;
            EA = DefaultBase(SS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_003 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 7;
            EO = (I.regs.w[BP] + I.regs.w[DI]) & 0xFFFF;
            EA = DefaultBase(SS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_004 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 5;
            EO = I.regs.w[SI];
            EA = DefaultBase(DS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_005 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 5;
            EO = I.regs.w[DI];
            EA = DefaultBase(DS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_006 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 6;
            EO = FETCHOP();
            EO += FETCHOP() << 8;
            EA = DefaultBase(DS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_007 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 5;
            EO = I.regs.w[BX];
            EA = DefaultBase(DS) + EO;
            return EA;
        }
    };

    static GetEAPtr EA_100 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 11;
            EO = (I.regs.w[BX] + I.regs.w[SI] + (byte) FETCHOP()) & 0xFFFF;
            EA = DefaultBase(DS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_101 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 12;
            EO = (I.regs.w[BX] + I.regs.w[DI] + (byte) FETCHOP()) & 0xFFFF;
            EA = DefaultBase(DS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_102 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 12;
            EO = (I.regs.w[BP] + I.regs.w[SI] + (byte) FETCHOP()) & 0xFFFF;
            EA = DefaultBase(SS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_103 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 11;
            EO = (I.regs.w[BP] + I.regs.w[DI] + (byte) FETCHOP()) & 0xFFFF;
            EA = DefaultBase(SS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_104 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 9;
            EO = (I.regs.w[SI] + (byte) FETCHOP()) & 0xFFFF;
            EA = DefaultBase(DS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_105 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 9;
            EO = (I.regs.w[DI] + (byte) FETCHOP()) & 0xFFFF;
            EA = DefaultBase(DS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_106 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 9;
            EO = (I.regs.w[BP] + (byte) FETCHOP()) & 0xFFFF;
            EA = DefaultBase(SS) + EO;
            return EA;
        }
    };
    static GetEAPtr EA_107 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 9;
            EO = (I.regs.w[BX] + (byte) FETCHOP()) & 0xFFFF;
            EA = DefaultBase(DS) + EO;
            return EA;
        }
    };

    static GetEAPtr EA_200 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 11;
            EO = FETCHOP();
            EO += FETCHOP() << 8;
            EO += I.regs.w[BX] + I.regs.w[SI];
            EA = DefaultBase(DS) + (EO & 0xFFFF);
            return EA;
        }
    };
    static GetEAPtr EA_201 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 12;
            EO = FETCHOP();
            EO += FETCHOP() << 8;
            EO += I.regs.w[BX] + I.regs.w[DI];
            EA = DefaultBase(DS) + (EO & 0xFFFF);
            return EA;
        }
    };
    static GetEAPtr EA_202 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 12;
            EO = FETCHOP();
            EO += FETCHOP() << 8;
            EO += I.regs.w[BP] + I.regs.w[SI];
            EA = DefaultBase(SS) + (EO & 0xFFFF);
            return EA;
        }
    };
    static GetEAPtr EA_203 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 11;
            EO = FETCHOP();
            EO += FETCHOP() << 8;
            EO += I.regs.w[BP] + I.regs.w[DI];
            EA = DefaultBase(SS) + (EO & 0xFFFF);
            return EA;
        }
    };
    static GetEAPtr EA_204 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 9;
            EO = FETCHOP();
            EO += FETCHOP() << 8;
            EO += I.regs.w[SI];
            EA = DefaultBase(DS) + (EO & 0xFFFF);
            return EA;
        }
    };
    static GetEAPtr EA_205 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 9;
            EO = FETCHOP();
            EO += FETCHOP() << 8;
            EO += I.regs.w[DI];
            EA = DefaultBase(DS) + (EO & 0xFFFF);
            return EA;
        }
    };
    static GetEAPtr EA_206 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 9;
            EO = FETCHOP();
            EO += FETCHOP() << 8;
            EO += I.regs.w[BP];
            EA = DefaultBase(SS) + (EO & 0xFFFF);
            return EA;
        }
    };
    static GetEAPtr EA_207 = new GetEAPtr() {
        public int handler() {
            i86_ICount[0] -= 9;
            EO = FETCHOP();
            EO += FETCHOP() << 8;
            EO += I.regs.w[BX];
            EA = DefaultBase(DS) + (EO & 0xFFFF);
            return EA;
        }
    };

    public static GetEAPtr[] GetEA
            = {
                EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
                EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
                EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
                EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
                EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
                EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
                EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
                EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
                EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
                EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
                EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
                EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
                EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
                EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
                EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
                EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
                EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
                EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
                EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
                EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
                EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
                EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
                EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
                EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207
            };

    public static abstract interface GetEAPtr {

        public abstract int handler();

    };
}
