/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.m6800;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.m6800.m6800.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.m6800.m6800Η.M6800_WAI;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;

public class m6800ops {

    public static opcode illegal = new opcode() {
        public void handler() {
            logerror("M6808: illegal opcode: address %04X, op %02X\n", m6800.pc, (int) M_RDOP_ARG(m6800.pc) & 0xFF);
        }
    };

    /* HD63701 only */
    //INLINE void trap = new opcode() {public void handler() {
    public static opcode trap = new opcode() {
        public void handler() {
            logerror("M6808: illegal opcode: address %04X, op %02X\n", m6800.pc, (int) M_RDOP_ARG(m6800.pc) & 0xFF);
            TAKE_TRAP();
        }
    };

    /* $01 NOP */
    public static opcode nop = new opcode() {
        public void handler() {
        }
    };
    /*TODO*///
/*TODO*////* $02 ILLEGAL */
/*TODO*///
/*TODO*////* $03 ILLEGAL */
/*TODO*///
/*TODO*////* $04 LSRD inherent -0*-* */
    public static opcode lsrd = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16 t;
/*TODO*///	CLR_NZC; t = D; CC|=(t&0x0001);
/*TODO*///	t>>=1; SET_Z16(t); D=t;
        }
    };

    public static opcode asld = new opcode() {
        public void handler() {
            int r;
            int t;
            t = getDreg();
            r = (t << 1) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS16(t, t, r);
            setDreg(r);
        }
    };

    public static opcode tap = new opcode() {
        public void handler() {
            m6800.cc = m6800.a & 0xFF;
            ONE_MORE_INSN();
            CHECK_IRQ_LINES();
        }
    };
    public static opcode tpa = new opcode() {
        public void handler() {
            m6800.a = m6800.cc & 0xFF;//A = CC;
        }
    };

    public static opcode inx = new opcode() {
        public void handler() {
            m6800.x = (m6800.x + 1) & 0xFFFF;//++X;
            CLR_Z();
            SET_Z16(m6800.x);
        }
    };

    public static opcode dex = new opcode() {
        public void handler() {
            m6800.x = (m6800.x - 1) & 0xFFFF;//--X;
            CLR_Z();
            SET_Z16(m6800.x);
        }
    };
    public static opcode clv = new opcode() {
        public void handler() {
            CLV();
        }
    };
    public static opcode sev = new opcode() {
        public void handler() {
            SEV();
        }
    };

    public static opcode clc = new opcode() {
        public void handler() {
            CLC();
        }
    };

    public static opcode sec = new opcode() {
        public void handler() {
            SEC();
        }
    };

    public static opcode cli = new opcode() {
        public void handler() {
            CLI();
            ONE_MORE_INSN();
            CHECK_IRQ_LINES();
            /* HJB 990417 */

        }
    };

    public static opcode sei = new opcode() {
        public void handler() {
            SEI();
            ONE_MORE_INSN();
            CHECK_IRQ_LINES();
            /* HJB 990417 */

        }
    };

    public static opcode sba = new opcode() {
        public void handler() {
            /*UINT16*/
            int t;
            t = (m6800.a - m6800.b) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, m6800.b, t);
            m6800.a = t & 0xFF;
        }
    };

    public static opcode cba = new opcode() {
        public void handler() {
            /*UINT16*/
            int t;
            t = (m6800.a - m6800.b) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, m6800.b, t);
        }
    };
    /*TODO*///
/*TODO*////* $12 ILLEGAL */
    public static opcode undoc1 = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	X += RM( S + 1 );
        }
    };
    /*TODO*///
/*TODO*////* $13 ILLEGAL */
    public static opcode undoc2 = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	X += RM( S + 1 );
        }
    };
    /*TODO*///

    public static opcode tab = new opcode() {
        public void handler() {
            m6800.b = m6800.a & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode tba = new opcode() {
        public void handler() {
            m6800.a = m6800.b & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };

    /*TODO*///
/*TODO*////* $18 XGDX inherent ----- */ /* HD63701YO only */
    public static opcode xgdx = new opcode() {
        public void handler() {
            //throw new UnsupportedOperationException("Unsupported");
            /*UINT16*/
            int t = m6800.x & 0xFFFF;
            m6800.x = getDreg();
            setDreg(t);
        }
    };

    /*RECHECK*/
    public static opcode daa = new opcode() {
        public void handler() {
            int/*UINT8*/ msn, lsn;
            int/*UINT16*/ t, cf = 0;
            msn = m6800.a & 0xf0;
            lsn = m6800.a & 0x0f;
            if (lsn > 0x09 || (m6800.cc & 0x20) != 0) {
                cf |= 0x06;
            }
            if (msn > 0x80 && lsn > 0x09) {
                cf |= 0x60;
            }
            if (msn > 0x90 || (m6800.cc & 0x01) != 0) {
                cf |= 0x60;
            }
            t = cf + m6800.a;
            CLR_NZV();
            /* keep carry from previous operation */

            SET_NZ8(/*(UINT8)*/t & 0xFF);
            SET_C8(t);
            m6800.a = t & 0xFF;
        }
    };
    /*TODO*///
/*TODO*////* $1a ILLEGAL */
/*TODO*///
/*TODO*///#if (HAS_HD63701)
/*TODO*////* $1a SLP */ /* HD63701YO only */
    public static opcode slp = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	/* wait for next IRQ (same as waiting of wai) */
/*TODO*///	m6808.wai_state |= HD63701_SLP;
/*TODO*///	EAT_CYCLES;
        }
    };

    public static opcode aba = new opcode() {
        public void handler() {
            /*UINT16*/
            int t;
            t = (m6800.a + m6800.b) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.a, m6800.b, t);
            SET_H(m6800.a, m6800.b, t);
            m6800.a = t & 0xFF;
        }
    };
    /*TODO*///
    public static opcode bra = new opcode() {
        public void handler() {
            int t;
            t = IMMBYTE();
            m6800.pc = (m6800.pc + (byte) t) & 0xFFFF;//TODO check if it has to be better...
            CHANGE_PC();
            /* speed up busy loops */
            if (t == 0xfe) {
                EAT_CYCLES();
            }
        }
    };
    public static opcode brn = new opcode() {
        public void handler() {
            int t = IMMBYTE();

        }
    };

    public static opcode bhi = new opcode() {
        public void handler() {
            BRANCH((m6800.cc & (0x05)) == 0);
        }
    };

    public static opcode bls = new opcode() {
        public void handler() {
            BRANCH((m6800.cc & (0x05)) != 0);

        }
    };

    public static opcode bcc = new opcode() {
        public void handler() {
            BRANCH((m6800.cc & 0x01) == 0);
        }
    };

    public static opcode bcs = new opcode() {
        public void handler() {
            BRANCH((m6800.cc & 0x01) != 0);
        }
    };

    public static opcode bne = new opcode() {
        public void handler() {
            BRANCH((m6800.cc & 0x04) == 0);
        }
    };
    public static opcode beq = new opcode() {
        public void handler() {
            BRANCH((m6800.cc & 0x04) != 0);
        }
    };

    public static opcode bvc = new opcode() {
        public void handler() {
            BRANCH((m6800.cc & 0x02) == 0);
        }
    };

    public static opcode bvs = new opcode() {
        public void handler() {
            BRANCH((m6800.cc & 0x02) != 0);
        }
    };

    public static opcode bpl = new opcode() {
        public void handler() {
            BRANCH((m6800.cc & 0x08) == 0);
        }
    };

    public static opcode bmi = new opcode() {
        public void handler() {
            BRANCH((m6800.cc & 0x08) != 0);
        }
    };

    public static opcode bge = new opcode() {
        public void handler() {
            BRANCH(NXORV() == 0);
        }
    };

    public static opcode blt = new opcode() {
        public void handler() {
            BRANCH(NXORV() != 0);
        }
    };

    public static opcode bgt = new opcode() {
        public void handler() {
            BRANCH(!((NXORV() != 0) || ((m6800.cc & 0x04) != 0)));
        }
    };

    public static opcode ble = new opcode() {
        public void handler() {
            BRANCH(((NXORV() != 0) || ((m6800.cc & 0x04) != 0)));
        }
    };

    public static opcode tsx = new opcode() {
        public void handler() {
            m6800.x = (m6800.s + 1) & 0xFFFF;
        }
    };

    public static opcode ins = new opcode() {
        public void handler() {
            m6800.s = (m6800.s + 1) & 0xFFFF; //++S;
        }
    };

    public static opcode pula = new opcode() {
        public void handler() {
            m6800.a = PULLBYTE();//PULLBYTE(m6808.d.b.h);

        }
    };

    public static opcode pulb = new opcode() {
        public void handler() {
            m6800.b = PULLBYTE();////PULLBYTE(m6808.d.b.l);          
        }
    };

    public static opcode des = new opcode() {
        public void handler() {
            m6800.s = (m6800.s - 1) & 0xFFFF;//--S;
        }
    };

    public static opcode txs = new opcode() {
        public void handler() {
            m6800.s = (m6800.x - 1) & 0xFFFF;//S = (X - 1);
        }
    };
    public static opcode psha = new opcode() {
        public void handler() {
            PUSHBYTE(m6800.a);//PUSHBYTE(m6808.d.b.h);
        }
    };

    public static opcode pshb = new opcode() {
        public void handler() {
            PUSHBYTE(m6800.b);//PUSHBYTE(m6808.d.b.l);
        }
    };

    public static opcode pulx = new opcode() {
        public void handler() {
            m6800.x = PULLWORD();
        }
    };

    public static opcode rts = new opcode() {
        public void handler() {
            m6800.pc = PULLWORD();
            CHANGE_PC();
        }
    };

    public static opcode abx = new opcode() {
        public void handler() {
            m6800.x = (m6800.x + m6800.b) & 0xFFFF;//X += B;
        }
    };

    public static opcode rti = new opcode() {
        public void handler() {
            m6800.cc = PULLBYTE();
            m6800.b = PULLBYTE();
            m6800.a = PULLBYTE();
            m6800.x = PULLWORD();
            m6800.pc = PULLWORD();
            CHANGE_PC();
            CHECK_IRQ_LINES();
        }
    };

    public static opcode pshx = new opcode() {
        public void handler() {
            PUSHWORD(m6800.x);
        }
    };

    /*TODO*////* $3d MUL inherent --*-@ */
    public static opcode mul = new opcode() {
        public void handler() {
            //throw new UnsupportedOperationException("Unsupported");
            int t;
            t = (m6800.a * m6800.b) & 0xFFFF;
            CLR_C();
            if ((t & 0x80) != 0) {
                SEC();
            }
            setDreg(t);
        }
    };
    /*TODO*///
    public static opcode wai = new opcode() {
        public void handler() {
            /*
             * WAI stacks the entire machine state on the
             * hardware stack, then waits for an interrupt.
             */
            m6800.wai_state |= M6800_WAI;
            PUSHWORD(m6800.pc);
            PUSHWORD(m6800.x);
            PUSHBYTE(m6800.a);
            PUSHBYTE(m6800.b);
            PUSHBYTE(m6800.cc);
            CHECK_IRQ_LINES();
            if ((m6800.wai_state & M6800_WAI) != 0) {
                EAT_CYCLES();
            }
        }
    };

    public static opcode swi = new opcode() {
        public void handler() {
            PUSHWORD(m6800.pc);
            PUSHWORD(m6800.x);
            PUSHBYTE(m6800.a);
            PUSHBYTE(m6800.b);
            PUSHBYTE(m6800.cc);
            SEI();
            m6800.pc = RM16(0xfffa) & 0xFFFF;
            CHANGE_PC();
        }
    };

    public static opcode nega = new opcode() {
        public void handler() {
            int r;
            r = -m6800.a & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(0, m6800.a, r);
            m6800.a = r & 0xFF;
        }
    };

    public static opcode coma = new opcode() {
        public void handler() {
            m6800.a = ~m6800.a & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.a);
            SEC();
        }
    };

    public static opcode lsra = new opcode() {
        public void handler() {
            CLR_NZC();
            m6800.cc |= (m6800.a & 0x01);
            m6800.a = (m6800.a >>> 1) & 0xFF;
            SET_Z8(m6800.a);
        }
    };

    public static opcode rora = new opcode() {
        public void handler() {
            int r;
            r = ((m6800.cc & 0x01) << 7) & 0xFF;
            CLR_NZC();
            m6800.cc |= (m6800.a & 0x01);
            r = (r | m6800.a >>> 1) & 0xFF;
            SET_NZ8(r);
            m6800.a = r & 0xFF;
        }
    };

    public static opcode asra = new opcode() {
        public void handler() {
            CLR_NZC();
            m6800.cc |= (m6800.a & 0x01);
            m6800.a = (m6800.a >>> 1) & 0xFF;
            m6800.a |= ((m6800.a & 0x40) << 1);
            SET_NZ8(m6800.a);

        }
    };

    public static opcode asla = new opcode() {
        public void handler() {
            int r = (m6800.a << 1) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, m6800.a, r);
            m6800.a = r & 0xFF;
        }
    };

    /*RECKECK*/
    public static opcode rola = new opcode() {
        public void handler() {
            int t, r;
            t = m6800.a & 0xFFFF;
            r = ((m6800.cc & 0x01));
            r = (r | t << 1) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(t, t, r);
            m6800.a = r & 0xFF;
        }
    };

    public static opcode deca = new opcode() {
        public void handler() {
            m6800.a = (m6800.a - 1) & 0xFF;//--A;
            CLR_NZV();
            SET_FLAGS8D(m6800.a);
        }
    };

    public static opcode inca = new opcode() {
        public void handler() {
            m6800.a = (m6800.a + 1) & 0xFF;//++A;
            CLR_NZV();
            SET_FLAGS8I(m6800.a);
        }
    };

    public static opcode tsta = new opcode() {
        public void handler() {
            CLR_NZVC();
            SET_NZ8(m6800.a);
        }
    };

    public static opcode clra = new opcode() {
        public void handler() {
            m6800.a = 0;
            CLR_NZVC();
            SEZ();
        }
    };

    public static opcode negb = new opcode() {
        public void handler() {
            int r;
            r = -m6800.b & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(0, m6800.b, r);
            m6800.b = r & 0xFF;
        }
    };

    public static opcode comb = new opcode() {
        public void handler() {
            m6800.b = ~m6800.b & 0xFF;//B = ~B;
            CLR_NZV();
            SET_NZ8(m6800.b);
            SEC();
        }
    };

    public static opcode lsrb = new opcode() {
        public void handler() {
            CLR_NZC();
            m6800.cc |= (m6800.b & 0x01);
            m6800.b = (m6800.b >>> 1) & 0xFF;
            SET_Z8(m6800.b);
        }
    };

    public static opcode rorb = new opcode() {
        public void handler() {
            int r;
            r = ((m6800.cc & 0x01) << 7) & 0xFF;
            CLR_NZC();
            m6800.cc |= (m6800.b & 0x01);
            r = (r | m6800.b >>> 1) & 0xFF;
            SET_NZ8(r);
            m6800.b = r & 0xFF;
        }
    };

    public static opcode asrb = new opcode() {
        public void handler() {
            CLR_NZC();
            m6800.cc |= (m6800.b & 0x01);
            m6800.b = (m6800.b >>> 1) & 0xFF;
            m6800.b |= ((m6800.b & 0x40) << 1);
            SET_NZ8(m6800.b);
        }
    };

    public static opcode aslb = new opcode() {
        public void handler() {
            int r = (m6800.b << 1) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.b, m6800.b, r);
            m6800.b = r & 0xFF;

        }
    };

    public static opcode rolb = new opcode() {
        public void handler() {
            int t, r;
            t = m6800.b & 0xFFFF;
            r = m6800.cc & 0x01;
            r = (r | t << 1) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(t, t, r);
            m6800.b = r & 0xFF;
        }
    };

    public static opcode decb = new opcode() {
        public void handler() {
            m6800.b = (m6800.b - 1) & 0xFF;
            CLR_NZV();
            SET_FLAGS8D(m6800.b);
        }
    };

    public static opcode incb = new opcode() {
        public void handler() {
            m6800.b = (m6800.b + 1) & 0xFF;  //++B;
            CLR_NZV();
            SET_FLAGS8I(m6800.b);
        }
    };

    public static opcode tstb = new opcode() {
        public void handler() {
            CLR_NZVC();
            SET_NZ8(m6800.b);
        }
    };
    public static opcode clrb = new opcode() {
        public void handler() {
            m6800.b = 0;
            CLR_NZVC();
            SEZ();
        }
    };

    public static opcode neg_ix = new opcode() {
        public void handler() {
            int r, t;
            t = IDXBYTE();
            r = -t & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(0, t, r);
            WM(ea, r & 0xFF);
        }
    };
    /*TODO*///
/*TODO*////* $61 AIM --**0- */ /* HD63701YO only */
    public static opcode aim_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t, r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	IDXBYTE(r);
/*TODO*///	r &= t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
/*TODO*///	WM(EAD,r);
        }
    };
    /*TODO*///
/*TODO*////* $62 OIM --**0- */ /* HD63701YO only */
    public static opcode oim_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t, r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	IDXBYTE(r);
/*TODO*///	r |= t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
/*TODO*///	WM(EAD,r);
        }
    };

    public static opcode com_ix = new opcode() {
        public void handler() {
            int t;
            t = IDXBYTE();
            t = ~t & 0xFF;
            CLR_NZV();
            SET_NZ8(t);
            SEC();
            WM(ea, t);
        }
    };

    public static opcode lsr_ix = new opcode() {
        public void handler() {
            int t;
            t = IDXBYTE();
            CLR_NZC();
            m6800.cc |= (t & 0x01);
            t = (t >>> 1) & 0xFF;
            SET_Z8(t);
            WM(ea, t);
        }
    };

    /*TODO*////* $65 EIM --**0- */ /* HD63701YO only */
    public static opcode eim_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t, r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	IDXBYTE(r);
/*TODO*///	r ^= t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
/*TODO*///	WM(EAD,r);
        }
    };
    /*TODO*///
/*TODO*////* $66 ROR indexed -**-* */
    public static opcode ror_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t,r;
/*TODO*///	IDXBYTE(t); r=(CC&0x01)<<7;
/*TODO*///	CLR_NZC; CC|=(t&0x01);
/*TODO*///	r |= t>>1; SET_NZ8(r);
/*TODO*///	WM(EAD,r);
        }
    };
    /*TODO*///
/*TODO*////* $67 ASR indexed ?**-* */
    public static opcode asr_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t;
/*TODO*///	IDXBYTE(t); CLR_NZC; CC|=(t&0x01);
/*TODO*///	t>>=1; t|=((t&0x40)<<1);
/*TODO*///	SET_NZ8(t);
/*TODO*///	WM(EAD,t);
        }
    };
    /*TODO*///
/*TODO*////* $68 ASL indexed ?**** */
    public static opcode asl_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16 t,r;
/*TODO*///	IDXBYTE(t); r=t<<1;
/*TODO*///	CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///	WM(EAD,r);
        }
    };

    public static opcode rol_ix = new opcode() {
        public void handler() {
            int t, r;
            t = IDXBYTE();
            r = m6800.cc & 0x01;
            r = (r | t << 1) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(t, t, r);
            WM(ea, r & 0xFF);
        }
    };

    public static opcode dec_ix = new opcode() {
        public void handler() {
            //UINT8 t;
            int t = IDXBYTE();
            t = (t - 1) & 0xFF;
            CLR_NZV();
            SET_FLAGS8D(t);
            WM(ea, t);

        }
    };

    /*TODO*////* $6b TIM --**0- */ /* HD63701YO only */
    public static opcode tim_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t, r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	IDXBYTE(r);
/*TODO*///	r &= t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
        }
    };
    /*TODO*///
    public static opcode inc_ix = new opcode() {
        public void handler() {
            int t = IDXBYTE();
            t = (t + 1) & 0xFF;
            CLR_NZV();
            SET_FLAGS8I(t);
            WM(ea, t);
        }
    };

    public static opcode tst_ix = new opcode() {
        public void handler() {
            int t = IDXBYTE();
            CLR_NZVC();
            SET_NZ8(t);
        }
    };

    public static opcode jmp_ix = new opcode() {
        public void handler() {
            INDEXED();
            m6800.pc = ea & 0xFFFF;
            CHANGE_PC();
        }
    };

    public static opcode clr_ix = new opcode() {
        public void handler() {
            INDEXED();
            WM(ea, 0);
            CLR_NZVC();
            SEZ();
        }
    };

    public static opcode neg_ex = new opcode() {
        public void handler() {
            int/*UINT16*/ r, t;
            t = EXTBYTE();
            r = -t & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(0, t, r);
            WM(ea, r & 0xFF);
        }
    };

    /* $71 AIM --**0- */ /* HD63701YO only */
    public static opcode aim_di = new opcode() {
        public void handler() {
            int t, r;
            t = IMMBYTE();
            r = DIRBYTE();
            r = (r & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(r);
            WM(ea, r);
        }
    };

    /* $72 OIM --**0- */ /* HD63701YO only */
    public static opcode oim_di = new opcode() {
        public void handler() {
            int t, r;
            t = IMMBYTE();
            r = DIRBYTE();
            r = (r | t) & 0xFF;
            CLR_NZV();
            SET_NZ8(r);
            WM(ea, r);
        }
    };

    public static opcode com_ex = new opcode() {
        public void handler() {
            int t = EXTBYTE();
            t = ~t & 0xFF;
            CLR_NZV();
            SET_NZ8(t);
            SEC();
            WM(ea, t);
        }
    };

    public static opcode lsr_ex = new opcode() {
        public void handler() {
            int t = EXTBYTE();
            CLR_NZC();
            m6800.cc |= (t & 0x01);
            t = (t >>> 1) & 0XFF;
            SET_Z8(t);
            WM(ea, t);
        }
    };

    /*TODO*///
/*TODO*////* $75 EIM --**0- */ /* HD63701YO only */
    public static opcode eim_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t, r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	DIRBYTE(r);
/*TODO*///	r ^= t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
/*TODO*///	WM(EAD,r);
        }
    };

    public static opcode ror_ex = new opcode() {
        public void handler() {
            int/*UINT8*/ t, r;
            t = EXTBYTE();
            r = ((m6800.cc & 0x01) << 7) & 0xFF;
            CLR_NZC();
            m6800.cc |= (t & 0x01);
            r = (r | t >>> 1) & 0xFF;
            SET_NZ8(r);
            WM(ea, r);
        }
    };

    /*TODO*////* $77 ASR extended ?**-* */
    public static opcode asr_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t); CLR_NZC; CC|=(t&0x01);
/*TODO*///	t>>=1; t|=((t&0x40)<<1);
/*TODO*///	SET_NZ8(t);
/*TODO*///	WM(EAD,t);
        }
    };

    public static opcode asl_ex = new opcode() {
        public void handler() {
            int/*UINT16*/ t, r;
            t = EXTBYTE();
            r = (t << 1) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(t, t, r);
            WM(ea, r);
        }
    };
    /*TODO*///
/*TODO*////* $79 ROL extended -**** */
    public static opcode rol_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16 t,r;
/*TODO*///	EXTBYTE(t); r = CC&0x01; r |= t<<1;
/*TODO*///	CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///	WM(EAD,r);
        }
    };
    /*TODO*///
    public static opcode dec_ex = new opcode() {
        public void handler() {
            int t = EXTBYTE();
            t = (t - 1) & 0xFF;
            CLR_NZV();
            SET_FLAGS8D(t);
            WM(ea, t);
        }
    };
    /*TODO*///
/*TODO*////* $7b TIM --**0- */ /* HD63701YO only */
    public static opcode tim_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t, r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	DIRBYTE(r);
/*TODO*///	r &= t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
        }
    };
    /*TODO*///
    public static opcode inc_ex = new opcode() {
        public void handler() {
            int t = EXTBYTE();
            t = (t + 1) & 0xFF;
            CLR_NZV();
            SET_FLAGS8I(t);
            WM(ea, t);
        }
    };

    /* $7d TST extended -**0- */
    public static opcode tst_ex = new opcode() {
        public void handler() {
            int t;
            t = EXTBYTE();
            CLR_NZVC();
            SET_NZ8(t);
        }
    };
    public static opcode jmp_ex = new opcode() {
        public void handler() {
            EXTENDED();
            m6800.pc = ea & 0xFFFF;
            CHANGE_PC();
            /* TS 971002 */

        }
    };

    /* $7f CLR extended -0100 */
    public static opcode clr_ex = new opcode() {
        public void handler() {
            EXTENDED();
            WM(ea, 0);
            CLR_NZVC();
            SEZ();
        }
    };

    public static opcode suba_im = new opcode() {
        public void handler() {
            int t, r;
            t = IMMBYTE();
            r = (m6800.a - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, t, r);
            m6800.a = r & 0xFF;
        }
    };

    public static opcode cmpa_im = new opcode() {
        public void handler() {
            int t, r;
            t = IMMBYTE();
            r = (m6800.a - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, t, r);
        }
    };

    /* $82 SBCA immediate ?**** */
    public static opcode sbca_im = new opcode() {
        public void handler() {
            /*UINT16*/
            int t, r;
            t = IMMBYTE();
            r = (m6800.a - t - (m6800.cc & 0x01)) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, t, r);
            m6800.a = r & 0xFF;
        }
    };

    /* $83 SUBD immediate -**** */
    public static opcode subd_im = new opcode() {
        public void handler() {
            //throw new UnsupportedOperationException("Unsupported");
            int r, d;
            int b;
            b = IMMWORD();
            d = getDreg();
            r = d - b;
            CLR_NZVC();
            SET_FLAGS16(d, b, r);
            setDreg(r);
        }
    };

    public static opcode anda_im = new opcode() {
        public void handler() {
            int t = IMMBYTE();
            m6800.a = (m6800.a & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };

    public static opcode bita_im = new opcode() {
        public void handler() {
            int t, r;
            t = IMMBYTE();
            r = (m6800.a & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(r);
        }
    };
    public static opcode lda_im = new opcode() {
        public void handler() {
            m6800.a = IMMBYTE();
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };
    /*TODO*///
/*TODO*////* is this a legal instruction? */
/*TODO*////* $87 STA immediate -**0- */
    public static opcode sta_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	CLR_NZV; SET_NZ8(A);
/*TODO*///	IMM8; WM(EAD,A);
        }
    };

    public static opcode eora_im = new opcode() {
        public void handler() {
            int t = IMMBYTE();
            m6800.a = (m6800.a ^ t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };

    public static opcode adca_im = new opcode() {
        public void handler() {
            int t, r;
            t = IMMBYTE();
            r = (m6800.a + t + (m6800.cc & 0x01)) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.a, t, r);
            SET_H(m6800.a, t, r);
            m6800.a = r & 0xFF;
        }
    };

    public static opcode ora_im = new opcode() {
        public void handler() {
            int t = IMMBYTE();
            m6800.a = (m6800.a | t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };

    public static opcode adda_im = new opcode() {
        public void handler() {
            int t, r;
            t = IMMBYTE();
            r = (m6800.a + t) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.a, t, r);
            SET_H(m6800.a, t, r);
            m6800.a = r & 0xFF;
        }
    };

    /*RECHECK*/
    public static opcode cmpx_im = new opcode() {
        public void handler() {
            int/*UINT32*/ r, d;
            int b = IMMWORD();
            d = m6800.x;
            r = (d - b); //&0xFFFF;//should be unsigned?
            CLR_NZV();
            SET_NZ16(r);
            SET_V16(d, b, r);
        }
    };
    /*TODO*///
/*TODO*////* $8c CPX immediate -**** (6803) */
    public static opcode cpx_im = new opcode() {
        public void handler() {
            //throw new UnsupportedOperationException("Unsupported");
            int r, d;
            int b;
            b = IMMWORD();
            d = m6800.x;
            r = d - b;
            CLR_NZVC();
            SET_FLAGS16(d, b, r);
        }
    };

    public static opcode bsr = new opcode() {
        public void handler() {
            int t = IMMBYTE();
            PUSHWORD(m6800.pc);
            m6800.pc = (m6800.pc + (byte) t) & 0xFFFF;
            CHANGE_PC();
        }
    };

    public static opcode lds_im = new opcode() {
        public void handler() {
            m6800.s = IMMWORD();
            CLR_NZV();
            SET_NZ16(m6800.s);
        }
    };
    /*TODO*///
/*TODO*////* $8f STS immediate -**0- */
    public static opcode sts_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
/*TODO*///	IMM16;
/*TODO*///	WM16(EAD,&m6808.s);
        }
    };

    public static opcode suba_di = new opcode() {
        public void handler() {
            /*UINT16*/
            int t, r;
            t = DIRBYTE();
            r = (m6800.a - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, t, r);
            m6800.a = r & 0xFF;
        }
    };

    public static opcode cmpa_di = new opcode() {
        public void handler() {
            int t, r;
            t = DIRBYTE();
            r = (m6800.a - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, t, r);
        }
    };
    public static opcode sbca_di = new opcode() {
        public void handler() {
            int/*UINT16*/ t, r;
            t = DIRBYTE();
            r = (m6800.a - t - (m6800.cc & 0x01)) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, t, r);
            m6800.a = r & 0xFF;
        }
    };
    /*TODO*///
/*TODO*////* $93 SUBD direct -**** */
    public static opcode subd_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	DIRWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D=r;
        }
    };

    public static opcode anda_di = new opcode() {
        public void handler() {
            int t = DIRBYTE();
            m6800.a = (m6800.a & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };

    public static opcode bita_di = new opcode() {
        public void handler() {
            int t, r;
            t = DIRBYTE();
            r = (m6800.a & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(r);
        }
    };

    public static opcode lda_di = new opcode() {
        public void handler() {
            m6800.a = DIRBYTE();
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };

    public static opcode sta_di = new opcode() {
        public void handler() {
            CLR_NZV();
            SET_NZ8(m6800.a);
            DIRECT();
            WM(ea, m6800.a);
        }
    };

    public static opcode eora_di = new opcode() {
        public void handler() {
            int t = DIRBYTE();
            m6800.a = (m6800.a ^ t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };

    public static opcode adca_di = new opcode() {
        public void handler() {
            int/*UINT16*/ t, r;
            t = DIRBYTE();
            r = (m6800.a + t + (m6800.cc & 0x01)) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.a, t, r);
            SET_H(m6800.a, t, r);
            m6800.a = r & 0xFF;
        }
    };

    public static opcode ora_di = new opcode() {
        public void handler() {
            int t = DIRBYTE();
            m6800.a = (m6800.a | t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };

    public static opcode adda_di = new opcode() {
        public void handler() {
            /*UINT16*/
            int t, r;
            t = DIRBYTE();
            r = (m6800.a + t) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.a, t, r);
            SET_H(m6800.a, t, r);
            m6800.a = r & 0xFF;
        }
    };
    /*RECKECK*/
    public static opcode cmpx_di = new opcode() {
        public void handler() {
            int/*UINT32*/ r, d;
            int b;
            b = DIRWORD();
            d = m6800.x;
            r = d - b;
            CLR_NZV();
            SET_NZ16(r);
            SET_V16(d, b, r);
        }
    };

    /*RECKECK*/
    public static opcode cpx_di = new opcode() {
        public void handler() {
            /*UINT32*/
            int r, d;
            int b;
            b = DIRWORD();
            d = m6800.x;
            r = d - b;
            CLR_NZVC();
            SET_FLAGS16(d, b, r);
        }
    };

    public static opcode jsr_di = new opcode() {
        public void handler() {
            DIRECT();
            PUSHWORD(m6800.pc);
            m6800.pc = ea & 0xFFFF;
            CHANGE_PC();
        }
    };

    public static opcode lds_di = new opcode() {
        public void handler() {
            m6800.s = DIRWORD();
            CLR_NZV();
            SET_NZ16(m6800.s);
        }
    };

    public static opcode sts_di = new opcode() {
        public void handler() {
            CLR_NZV();
            SET_NZ16(m6800.s);
            DIRECT();
            WM16(ea, m6800.s);
        }
    };

    public static opcode suba_ix = new opcode() {
        public void handler() {
            int t, r;
            t = IDXBYTE();
            r = (m6800.a - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, t, r);
            m6800.a = r & 0xFF;
        }
    };

    public static opcode cmpa_ix = new opcode() {
        public void handler() {
            /*UINT16*/
            int t, r;
            t = IDXBYTE();
            r = (m6800.a - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, t, r);
        }
    };
    /*TODO*///
/*TODO*////* $a2 SBCA indexed ?**** */
    public static opcode sbca_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16	  t,r;
/*TODO*///	IDXBYTE(t); r = A-t-(CC&0x01);
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
        }
    };
    /*TODO*///
/*TODO*////* $a3 SUBD indexed -**** */
    public static opcode subd_ix = new opcode() {
        public void handler() {
            //throw new UnsupportedOperationException("Unsupported");
            int r, d;
            int b;
            b = IDXWORD();
            d = getDreg();
            r = d - b;
            CLR_NZVC();
            SET_FLAGS16(d, b, r);
            setDreg(r);
        }
    };

    public static opcode anda_ix = new opcode() {
        public void handler() {
            /*UINT8*/
            int t;
            t = IDXBYTE();
            m6800.a = (m6800.a & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };
    /*TODO*///
/*TODO*////* $a5 BITA indexed -**0- */
    public static opcode bita_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t,r;
/*TODO*///	IDXBYTE(t); r = A&t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
        }
    };

    public static opcode lda_ix = new opcode() {
        public void handler() {
            m6800.a = IDXBYTE();
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };

    public static opcode sta_ix = new opcode() {
        public void handler() {
            CLR_NZV();
            SET_NZ8(m6800.a);
            INDEXED();
            WM(ea, m6800.a);
        }
    };

    public static opcode eora_ix = new opcode() {
        public void handler() {
            /*UINT8*/
            int t;
            t = IDXBYTE();
            m6800.a = (m6800.a ^ t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };
    /*TODO*///
/*TODO*////* $a9 ADCA indexed ***** */
    public static opcode adca_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16 t,r;
/*TODO*///	IDXBYTE(t); r = A+t+(CC&0x01);
/*TODO*///	CLR_HNZVC; SET_FLAGS8(A,t,r); SET_H(A,t,r);
/*TODO*///	A = r;
        }
    };

    public static opcode ora_ix = new opcode() {
        public void handler() {
            int t;
            t = IDXBYTE();
            m6800.a = (m6800.a | t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };

    public static opcode adda_ix = new opcode() {
        public void handler() {
            int t, r;
            t = IDXBYTE();
            r = (m6800.a + t) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.a, t, r);
            SET_H(m6800.a, t, r);
            m6800.a = r & 0xFF;
        }
    };
    /*TODO*///
/*TODO*////* $ac CMPX indexed -***- */
    public static opcode cmpx_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IDXWORD(b);
/*TODO*///	d = X;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(r); SET_V16(d,b.d,r);
        }
    };
    /*TODO*///
/*TODO*////* $ac CPX indexed -**** (6803)*/
    public static opcode cpx_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IDXWORD(b);
/*TODO*///	d = X;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC; SET_FLAGS16(d,b.d,r);
        }
    };

    public static opcode jsr_ix = new opcode() {
        public void handler() {
            INDEXED();
            PUSHWORD(m6800.pc);
            m6800.pc = ea & 0xFFFF;
            CHANGE_PC();
        }
    };

    public static opcode lds_ix = new opcode() {
        public void handler() {
            m6800.s = IDXWORD();
            CLR_NZV();
            SET_NZ16(m6800.s);
        }
    };
    /*TODO*///
/*TODO*////* $af STS indexed -**0- */
    public static opcode sts_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
/*TODO*///	INDEXED;
/*TODO*///	WM16(EAD,&m6808.s);
        }
    };

    public static opcode suba_ex = new opcode() {
        public void handler() {
            int t, r;
            t = EXTBYTE();
            r = (m6800.a - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, t, r);
            m6800.a = r & 0xFF;
        }
    };

    public static opcode cmpa_ex = new opcode() {
        public void handler() {
            int t, r;
            t = EXTBYTE();
            r = (m6800.a - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.a, t, r);
        }
    };
    /*TODO*///
/*TODO*////* $b2 SBCA extended ?**** */
    public static opcode sbca_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16	  t,r;
/*TODO*///	EXTBYTE(t); r = A-t-(CC&0x01);
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
        }
    };
    /*TODO*///
/*TODO*////* $b3 SUBD extended -**** */
    public static opcode subd_ex = new opcode() {
        public void handler() {
            //throw new UnsupportedOperationException("Unsupported");
            int r, d;
            int b;
            b = EXTWORD();
            d = getDreg();
            r = d - b;
            CLR_NZVC();
            SET_FLAGS16(d, b, r);
            setDreg(r);
        }
    };
    /*TODO*///
/*TODO*////* $b4 ANDA extended -**0- */
    public static opcode anda_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t); A &= t;
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $b5 BITA extended -**0- */
    public static opcode bita_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t,r;
/*TODO*///	EXTBYTE(t); r = A&t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
        }
    };

    public static opcode lda_ex = new opcode() {
        public void handler() {
            m6800.a = EXTBYTE();
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };

    public static opcode sta_ex = new opcode() {
        public void handler() {
            CLR_NZV();
            SET_NZ8(m6800.a);
            EXTENDED();
            WM(ea, m6800.a);
        }
    };
    /*TODO*///
/*TODO*////* $b8 EORA extended -**0- */
    public static opcode eora_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t); A ^= t;
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $b9 ADCA extended ***** */
    public static opcode adca_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16 t,r;
/*TODO*///	EXTBYTE(t); r = A+t+(CC&0x01);
/*TODO*///	CLR_HNZVC; SET_FLAGS8(A,t,r); SET_H(A,t,r);
/*TODO*///	A = r;
        }
    };

    public static opcode ora_ex = new opcode() {
        public void handler() {
            int t = EXTBYTE();
            m6800.a = (m6800.a | t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.a);
        }
    };

    public static opcode adda_ex = new opcode() {
        public void handler() {
            int t, r;
            t = EXTBYTE();
            r = (m6800.a + t) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.a, t, r);
            SET_H(m6800.a, t, r);
            m6800.a = r & 0xFF;
        }
    };

    /*RECKECK*/
    public static opcode cmpx_ex = new opcode() {
        public void handler() {
            int/*UINT32*/ r, d;
            int b;
            b = EXTWORD();
            d = m6800.x;
            r = d - b;
            CLR_NZV();
            SET_NZ16(r);
            SET_V16(d, b, r);
        }
    };
    /*TODO*///
/*TODO*////* $bc CPX extended -**** (6803) */
    public static opcode cpx_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	EXTWORD(b);
/*TODO*///	d = X;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC; SET_FLAGS16(d,b.d,r);
        }
    };

    public static opcode jsr_ex = new opcode() {
        public void handler() {
            EXTENDED();
            PUSHWORD(m6800.pc);
            m6800.pc = ea & 0xFFFF;
            CHANGE_PC();
        }
    };

    public static opcode lds_ex = new opcode() {
        public void handler() {
            m6800.s = EXTWORD();
            CLR_NZV();
            SET_NZ16(m6800.s);
        }
    };
    /*TODO*///
/*TODO*////* $bf STS extended -**0- */
    public static opcode sts_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
/*TODO*///	EXTENDED;
/*TODO*///	WM16(EAD,&m6808.s);
        }
    };

    public static opcode subb_im = new opcode() {
        public void handler() {
            int t, r;
            t = IMMBYTE();
            r = (m6800.b - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.b, t, r);
            m6800.b = r & 0xFF;
        }
    };

    public static opcode cmpb_im = new opcode() {
        public void handler() {
            int t, r;
            t = IMMBYTE();
            r = (m6800.b - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.b, t, r);
        }
    };

    public static opcode sbcb_im = new opcode() {
        public void handler() {
            /*UINT16*/
            int t, r;
            t = IMMBYTE();
            r = (m6800.b - t - (m6800.cc & 0x01)) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.b, t, r);
            m6800.b = r & 0xFF;
        }
    };
    /*TODO*///
/*TODO*////* $c3 ADDD immediate -**** */
    public static opcode addd_im = new opcode() {
        public void handler() {
            //throw new UnsupportedOperationException("Unsupported");
            int r, d;
            int b;
            b = IMMWORD();
            d = getDreg();
            r = d + b;
            CLR_NZVC();
            SET_FLAGS16(d, b, r);
            setDreg(r);
        }
    };

    public static opcode andb_im = new opcode() {
        public void handler() {
            int t = IMMBYTE();
            m6800.b = (m6800.b & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode bitb_im = new opcode() {
        public void handler() {
            int t, r;
            t = IMMBYTE();
            r = (m6800.b & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(r);
        }
    };

    public static opcode ldb_im = new opcode() {
        public void handler() {
            m6800.b = IMMBYTE();
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };
    /*TODO*///
/*TODO*////* is this a legal instruction? */
/*TODO*////* $c7 STB immediate -**0- */
    public static opcode stb_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	CLR_NZV; SET_NZ8(B);
/*TODO*///	IMM8; WM(EAD,B);
        }
    };

    public static opcode eorb_im = new opcode() {
        public void handler() {
            int t = IMMBYTE();
            m6800.b = (m6800.b ^ t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode adcb_im = new opcode() {
        public void handler() {
            int t, r;
            t = IMMBYTE();
            r = (m6800.b + t + (m6800.cc & 0x01)) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.b, t, r);
            SET_H(m6800.b, t, r);
            m6800.b = r & 0xFF;
        }
    };

    public static opcode orb_im = new opcode() {
        public void handler() {
            int t = IMMBYTE();
            m6800.b = (m6800.b | t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode addb_im = new opcode() {
        public void handler() {
            int t, r;
            t = IMMBYTE();
            r = (m6800.b + t) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.b, t, r);
            SET_H(m6800.b, t, r);
            m6800.b = r & 0xFF;
        }
    };

    /* $CC LDD immediate -**0- */
    public static opcode ldd_im = new opcode() {
        public void handler() {
            //throw new UnsupportedOperationException("Unsupported");
            int tmp = IMMWORD();
            setDreg(tmp);
            CLR_NZV();
            SET_NZ16(tmp);
        }
    };

    /*TODO*////* is this a legal instruction? */
/*TODO*////* $cd STD immediate -**0- */
    public static opcode std_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	IMM16;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
/*TODO*///	WM16(EAD,&m6808.d);
        }
    };

    public static opcode ldx_im = new opcode() {
        public void handler() {
            m6800.x = IMMWORD();
            CLR_NZV();
            SET_NZ16(m6800.x);
        }
    };
    /*TODO*///
/*TODO*////* $cf STX immediate -**0- */
    public static opcode stx_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
/*TODO*///	IMM16;
/*TODO*///	WM16(EAD,&m6808.x);
        }
    };

    public static opcode subb_di = new opcode() {
        public void handler() {
            int t, r;
            t = DIRBYTE();
            r = (m6800.b - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.b, t, r);
            m6800.b = r & 0xFF;
        }
    };

    public static opcode cmpb_di = new opcode() {
        public void handler() {
            int t, r;
            t = DIRBYTE();
            r = (m6800.b - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.b, t, r);
        }
    };
    /*TODO*///
/*TODO*////* $d2 SBCB direct ?**** */
    public static opcode sbcb_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16	  t,r;
/*TODO*///	DIRBYTE(t); r = B-t-(CC&0x01);
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
        }
    };
    /*TODO*///
/*TODO*////* $d3 ADDD direct -**** */
    public static opcode addd_di = new opcode() {
        public void handler() {
            //throw new UnsupportedOperationException("Unsupported");
            int r, d;
            int b;
            b = DIRWORD();
            d = getDreg();
            r = d + b;
            CLR_NZVC();
            SET_FLAGS16(d, b, r);
            setDreg(r);
        }
    };

    public static opcode andb_di = new opcode() {
        public void handler() {
            int t = DIRBYTE();
            m6800.b = (m6800.b & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };
    /*TODO*///
/*TODO*////* $d5 BITB direct -**0- */
    public static opcode bitb_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT8 t,r;
/*TODO*///	DIRBYTE(t); r = B&t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
        }
    };

    public static opcode ldb_di = new opcode() {
        public void handler() {
            m6800.b = DIRBYTE();
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode stb_di = new opcode() {
        public void handler() {
            CLR_NZV();
            SET_NZ8(m6800.b);
            DIRECT();
            WM(ea, m6800.b);
        }
    };

    public static opcode eorb_di = new opcode() {
        public void handler() {
            int/*UINT8*/ t;
            t = DIRBYTE();
            m6800.b = (m6800.b ^ t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    /*TODO*////* $d9 ADCB direct ***** */
    public static opcode adcb_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16 t,r;
/*TODO*///	DIRBYTE(t); r = B+t+(CC&0x01);
/*TODO*///	CLR_HNZVC; SET_FLAGS8(B,t,r); SET_H(B,t,r);
/*TODO*///	B = r;
        }
    };

    public static opcode orb_di = new opcode() {
        public void handler() {
            int t = DIRBYTE();
            m6800.b = (m6800.b | t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode addb_di = new opcode() {
        public void handler() {
            int t, r;
            t = DIRBYTE();
            r = (m6800.b + t) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.b, t, r);
            SET_H(m6800.b, t, r);
            m6800.b = r & 0xFF;
        }
    };

    public static opcode ldd_di = new opcode() {
        public void handler() {
            int temp = DIRWORD();
            setDreg(temp);
            CLR_NZV();
            SET_NZ16(temp);
        }
    };

    public static opcode std_di = new opcode() {
        public void handler() {
            DIRECT();
            CLR_NZV();
            int temp = getDreg();
            SET_NZ16(temp);
            WM16(ea, temp);
        }
    };

    public static opcode ldx_di = new opcode() {
        public void handler() {
            m6800.x = DIRWORD();
            CLR_NZV();
            SET_NZ16(m6800.x);
        }
    };

    public static opcode stx_di = new opcode() {
        public void handler() {
            CLR_NZV();
            SET_NZ16(m6800.x);
            DIRECT();
            WM16(ea, m6800.x);
        }
    };

    /*TODO*////* $e0 SUBB indexed ?**** */
    public static opcode subb_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16	  t,r;
/*TODO*///	IDXBYTE(t); r = B-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
        }
    };

    public static opcode cmpb_ix = new opcode() {
        public void handler() {
            /*UINT16*/
            int t, r;
            t = IDXBYTE();
            r = (m6800.b - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.b, t, r);
        }
    };
    /*TODO*///
/*TODO*////* $e2 SBCB indexed ?**** */
    public static opcode sbcb_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16	  t,r;
/*TODO*///	IDXBYTE(t); r = B-t-(CC&0x01);
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
        }
    };
    
    public static opcode addd_ix = new opcode() {
        public void handler() {
            int r, d;
            int b;
            b = IDXWORD();
            d = getDreg();
            r = d + b;
            CLR_NZVC();
            SET_FLAGS16(d, b, r);
            setDreg(r);
        }
    };

    public static opcode andb_ix = new opcode() {
        public void handler() {
            int t = IDXBYTE();
            m6800.b = (m6800.b & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode bitb_ix = new opcode() {
        public void handler() {
            int t, r;
            t = IDXBYTE();
            r = (m6800.b & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(r);
        }
    };

    public static opcode ldb_ix = new opcode() {
        public void handler() {
            m6800.b = IDXBYTE();
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode stb_ix = new opcode() {
        public void handler() {
            CLR_NZV();
            SET_NZ8(m6800.b);
            INDEXED();
            WM(ea, m6800.b);
        }
    };

    public static opcode eorb_ix = new opcode() {
        public void handler() {
            int t = IDXBYTE();
            m6800.b = (m6800.b ^ t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode adcb_ix = new opcode() {
        public void handler() {
            int t, r;
            t = IDXBYTE();
            r = (m6800.b + t + (m6800.cc & 0x01)) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.b, t, r);
            SET_H(m6800.b, t, r);
            m6800.b = r & 0xFF;
        }
    };

    public static opcode orb_ix = new opcode() {
        public void handler() {
            int t;
            t = IDXBYTE();
            m6800.b = (m6800.b | t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode addb_ix = new opcode() {
        public void handler() {
            int t, r;
            t = IDXBYTE();
            r = (m6800.b + t) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.b, t, r);
            SET_H(m6800.b, t, r);
            m6800.b = r & 0xFF;
        }
    };

    public static opcode ldd_ix = new opcode() {
        public void handler() {
            int temp = IDXWORD();
            setDreg(temp);
            CLR_NZV();
            SET_NZ16(temp);
        }
    };

    public static opcode std_ix = new opcode() {
        public void handler() {
            INDEXED();
            CLR_NZV();
            int temp = getDreg();
            SET_NZ16(temp);
            WM16(ea, temp);
        }
    };

    public static opcode ldx_ix = new opcode() {
        public void handler() {
            m6800.x = IDXWORD();
            CLR_NZV();
            SET_NZ16(m6800.x);
        }
    };

    public static opcode stx_ix = new opcode() {
        public void handler() {
            CLR_NZV();
            SET_NZ16(m6800.x);
            INDEXED();
            WM16(ea, m6800.x);
        }
    };

    public static opcode subb_ex = new opcode() {
        public void handler() {
            int t, r;
            t = EXTBYTE();
            r = (m6800.b - t) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.b, t, r);
            m6800.b = r & 0xFF;
        }
    };
    /*TODO*///
/*TODO*////* $f1 CMPB extended ?**** */
    public static opcode cmpb_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16	  t,r;
/*TODO*///	EXTBYTE(t); r = B-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
        }
    };

    public static opcode sbcb_ex = new opcode() {
        public void handler() {
            int t, r;
            t = EXTBYTE();
            r = (m6800.b - t - (m6800.cc & 0x01)) & 0xFFFF;
            CLR_NZVC();
            SET_FLAGS8(m6800.b, t, r);
            m6800.b = r & 0xFF;
        }
    };
    /*TODO*///
/*TODO*////* $f3 ADDD extended -**** */
    public static opcode addd_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	EXTWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d + b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
        }
    };

    public static opcode andb_ex = new opcode() {
        public void handler() {
            int t = EXTBYTE();
            m6800.b = (m6800.b & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode bitb_ex = new opcode() {
        public void handler() {
            int t, r;
            t = EXTBYTE();
            r = (m6800.b & t) & 0xFF;
            CLR_NZV();
            SET_NZ8(r);
        }
    };

    public static opcode ldb_ex = new opcode() {
        public void handler() {
            m6800.b = EXTBYTE();
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode stb_ex = new opcode() {
        public void handler() {
            CLR_NZV();
            SET_NZ8(m6800.b);
            EXTENDED();
            WM(ea, m6800.b);
        }
    };

    public static opcode eorb_ex = new opcode() {
        public void handler() {
            int t = EXTBYTE();
            m6800.b = (m6800.b ^ t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };
    /*TODO*///
/*TODO*////* $f9 ADCB extended ***** */
    public static opcode adcb_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	UINT16 t,r;
/*TODO*///	EXTBYTE(t); r = B+t+(CC&0x01);
/*TODO*///	CLR_HNZVC; SET_FLAGS8(B,t,r); SET_H(B,t,r);
/*TODO*///	B = r;
        }
    };

    public static opcode orb_ex = new opcode() {
        public void handler() {
            int t = EXTBYTE();
            m6800.b = (m6800.b | t) & 0xFF;
            CLR_NZV();
            SET_NZ8(m6800.b);
        }
    };

    public static opcode addb_ex = new opcode() {
        public void handler() {
            int t, r;
            t = EXTBYTE();
            r = (m6800.b + t) & 0xFFFF;
            CLR_HNZVC();
            SET_FLAGS8(m6800.b, t, r);
            SET_H(m6800.b, t, r);
            m6800.b = r & 0xFF;
        }
    };
    /*TODO*///
/*TODO*////* $fc LDD extended -**0- */
    public static opcode ldd_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///	EXTWORD(m6808.d);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
        }
    };
    /*RECHECK*/
    public static opcode addx_ex = new opcode() {
        public void handler() {
            int /*UINT32*/ r, d;
            int b = EXTWORD();
            d = m6800.x;
            r = d + b;
            CLR_NZVC();
            SET_FLAGS16(d, b, r);
            m6800.x = r & 0xFFFF;
        }
    };

    public static opcode std_ex = new opcode() {
        public void handler() {
            EXTENDED();
            CLR_NZV();
            int temp = getDreg();
            SET_NZ16(temp);
            WM16(ea, temp);
        }
    };

    public static opcode ldx_ex = new opcode() {
        public void handler() {
            m6800.x = EXTWORD();
            CLR_NZV();
            SET_NZ16(m6800.x);
        }
    };
    public static opcode stx_ex = new opcode() {
        public void handler() {
            CLR_NZV();
            SET_NZ16(m6800.x);
            EXTENDED();
            WM16(ea, m6800.x);
        }
    };

    public static abstract interface opcode {

        public abstract void handler();
    }
}
