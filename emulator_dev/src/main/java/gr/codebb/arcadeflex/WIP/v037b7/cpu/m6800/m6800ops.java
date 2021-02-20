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

    /*TODO*///
/*TODO*////* HD63701 only */
/*TODO*/////INLINE void trap = new opcode() {public void handler() {
/*TODO*///static void trap = new opcode() {public void handler() {
/*TODO*///{
/*TODO*///	logerror("M6808: illegal opcode: address %04X, op %02X\n",PC,(int) M_RDOP_ARG(PC)&0xFF);
/*TODO*///	TAKE_TRAP;
/*TODO*///}};
/*TODO*///
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
    /*TODO*///
/*TODO*////* $05 ASLD inherent ?**** */
    public static opcode asld = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	int r;
/*TODO*///	UINT16 t;
/*TODO*///	t = D; r=t<<1;
/*TODO*///	CLR_NZVC; SET_FLAGS16(t,t,r);
/*TODO*///	D=r;
        }
    };
    /*TODO*///
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
    /*TODO*///
/*TODO*////* $10 SBA inherent -**** */
    public static opcode sba = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t;
/*TODO*///	t=A-B;
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,B,t);
/*TODO*///	A=t;
        }
    };
    /*TODO*///
/*TODO*////* $11 CBA inherent -**** */
    public static opcode cba = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t;
/*TODO*///	t=A-B;
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,B,t);
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
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t = X;
/*TODO*///	X = D;
/*TODO*///	D=t;
        }
    };
    /*TODO*///
/*TODO*////* $19 DAA inherent (A) -**0* */
    public static opcode daa = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 msn, lsn;
/*TODO*///	UINT16 t, cf = 0;
/*TODO*///	msn=A & 0xf0; lsn=A & 0x0f;
/*TODO*///	if( lsn>0x09 || CC&0x20 ) cf |= 0x06;
/*TODO*///	if( msn>0x80 && lsn>0x09 ) cf |= 0x60;
/*TODO*///	if( msn>0x90 || CC&0x01 ) cf |= 0x60;
/*TODO*///	t = cf + A;
/*TODO*///	CLR_NZV; /* keep carry from previous operation */
/*TODO*///	SET_NZ8((UINT8)t); SET_C8(t);
/*TODO*///	A = t;
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
    /*TODO*///#endif
/*TODO*///
/*TODO*////* $1b ABA inherent ***** */
    public static opcode aba = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t;
/*TODO*///	t=A+B;
/*TODO*///	CLR_HNZVC; SET_FLAGS8(A,B,t); SET_H(A,B,t);
/*TODO*///	A=t;
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
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t;
/*TODO*///	t=A*B;
/*TODO*///	CLR_C; if(t&0x80) SEC;
/*TODO*///	D=t;
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

    /*TODO*////* $40 NEGA inherent ?**** */
    public static opcode nega = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 r;
/*TODO*///	r=-A;
/*TODO*///	CLR_NZVC; SET_FLAGS8(0,A,r);
/*TODO*///	A=r;
        }
    };
    /*TODO*///
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

    /*TODO*////* $46 RORA inherent -**-* */
    public static opcode rora = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 r;
/*TODO*///	r=(CC&0x01)<<7;
/*TODO*///	CLR_NZC; CC|=(A&0x01);
/*TODO*///	r |= A>>1; SET_NZ8(r);
/*TODO*///	A=r;
        }
    };
    /*TODO*///
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

    /*TODO*///
/*TODO*////* $49 ROLA inherent -**** */
    public static opcode rola = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	t = A; r = CC&0x01; r |= t<<1;
/*TODO*///	CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///	A=r;
        }
    };
    /*TODO*///
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

    /*TODO*///
/*TODO*////* $50 NEGB inherent ?**** */
    public static opcode negb = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 r;
/*TODO*///	r=-B;
/*TODO*///	CLR_NZVC; SET_FLAGS8(0,B,r);
/*TODO*///	B=r;
        }
    };
    /*TODO*///
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
    /*TODO*////* $56 RORB inherent -**-* */
    public static opcode rorb = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 r;
/*TODO*///	r=(CC&0x01)<<7;
/*TODO*///	CLR_NZC; CC|=(B&0x01);
/*TODO*///	r |= B>>1; SET_NZ8(r);
/*TODO*///	B=r;
        }
    };
    /*TODO*///
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

    /*TODO*///
/*TODO*////* $59 ROLB inherent -**** */
    public static opcode rolb = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	t = B; r = CC&0x01; r |= t<<1;
/*TODO*///	CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///	B=r;
        }
    };
    /*TODO*///
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

    /*TODO*///#if macintosh
/*TODO*///#pragma mark ____6x____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $60 NEG indexed ?**** */
    public static opcode neg_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 r,t;
/*TODO*///	IDXBYTE(t); r=-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(0,t,r);
/*TODO*///	WM(EAD,r);
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
    /*TODO*///
/*TODO*////* $69 ROL indexed -**** */
    public static opcode rol_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	IDXBYTE(t); r = CC&0x01; r |= t<<1;
/*TODO*///	CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///	WM(EAD,r);
        }
    };
    /*TODO*///
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

    /*TODO*///#if macintosh
/*TODO*///#pragma mark ____7x____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $70 NEG extended ?**** */
    public static opcode neg_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 r,t;
/*TODO*///	EXTBYTE(t); r=-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(0,t,r);
/*TODO*///	WM(EAD,r);
        }
    };
    /*TODO*///
/*TODO*////* $71 AIM --**0- */ /* HD63701YO only */
    public static opcode aim_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t, r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	DIRBYTE(r);
/*TODO*///	r &= t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
/*TODO*///	WM(EAD,r);
        }
    };
    /*TODO*///
/*TODO*////* $72 OIM --**0- */ /* HD63701YO only */
    public static opcode oim_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t, r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	DIRBYTE(r);
/*TODO*///	r |= t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
/*TODO*///	WM(EAD,r);
        }
    };
    /*TODO*///
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
    /*TODO*///
/*TODO*////* $76 ROR extended -**-* */
    public static opcode ror_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t,r;
/*TODO*///	EXTBYTE(t); r=(CC&0x01)<<7;
/*TODO*///	CLR_NZC; CC|=(t&0x01);
/*TODO*///	r |= t>>1; SET_NZ8(r);
/*TODO*///	WM(EAD,r);
        }
    };
    /*TODO*///
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
    /*TODO*///
/*TODO*////* $78 ASL extended ?**** */
    public static opcode asl_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	EXTBYTE(t); r=t<<1;
/*TODO*///	CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///	WM(EAD,r);
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

    /*TODO*////* $80 SUBA immediate ?**** */
    public static opcode suba_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	IMMBYTE(t); r = A-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
        }
    };
    /*TODO*///
/*TODO*////* $81 CMPA immediate ?**** */
    public static opcode cmpa_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	IMMBYTE(t); r = A-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,t,r);
        }
    };
    /*TODO*///
/*TODO*////* $82 SBCA immediate ?**** */
    public static opcode sbca_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	IMMBYTE(t); r = A-t-(CC&0x01);
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
        }
    };
    /*TODO*///
/*TODO*////* $83 SUBD immediate -**** */
    public static opcode subd_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IMMWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
        }
    };
    /*TODO*///
/*TODO*////* $84 ANDA immediate -**0- */
    public static opcode anda_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t); A &= t;
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $85 BITA immediate -**0- */
    public static opcode bita_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t,r;
/*TODO*///	IMMBYTE(t); r = A&t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
        }
    };
    /*TODO*///
/*TODO*////* $86 LDA immediate -**0- */
    public static opcode lda_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	IMMBYTE(A);
/*TODO*///	CLR_NZV; SET_NZ8(A);
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
    /*TODO*///
/*TODO*////* $88 EORA immediate -**0- */
    public static opcode eora_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t); A ^= t;
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $89 ADCA immediate ***** */
    public static opcode adca_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	IMMBYTE(t); r = A+t+(CC&0x01);
/*TODO*///	CLR_HNZVC; SET_FLAGS8(A,t,r); SET_H(A,t,r);
/*TODO*///	A = r;
        }
    };
    /*TODO*///
/*TODO*////* $8a ORA immediate -**0- */
    public static opcode ora_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t); A |= t;
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $8b ADDA immediate ***** */
    public static opcode adda_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	IMMBYTE(t); r = A+t;
/*TODO*///	CLR_HNZVC; SET_FLAGS8(A,t,r); SET_H(A,t,r);
/*TODO*///	A = r;
        }
    };
    /*TODO*///
/*TODO*////* $8c CMPX immediate -***- */
    public static opcode cmpx_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IMMWORD(b);
/*TODO*///	d = X;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(r); SET_V16(d,b.d,r);
        }
    };
    /*TODO*///
/*TODO*////* $8c CPX immediate -**** (6803) */
    public static opcode cpx_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IMMWORD(b);
/*TODO*///	d = X;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC; SET_FLAGS16(d,b.d,r);
        }
    };
    /*TODO*///
/*TODO*///
/*TODO*////* $8d BSR ----- */
    public static opcode bsr = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	PUSHWORD(pPC);
/*TODO*///	PC += SIGNED(t);
/*TODO*///	CHANGE_PC();	 /* TS 971002 */
        }
    };
    /*TODO*///
/*TODO*////* $8e LDS immediate -**0- */
    public static opcode lds_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	IMMWORD(m6808.s);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
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
    /*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____9x____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $90 SUBA direct ?**** */
    public static opcode suba_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	DIRBYTE(t); r = A-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
        }
    };
    /*TODO*///
/*TODO*////* $91 CMPA direct ?**** */
    public static opcode cmpa_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	DIRBYTE(t); r = A-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,t,r);
        }
    };
    /*TODO*///
/*TODO*////* $92 SBCA direct ?**** */
    public static opcode sbca_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	DIRBYTE(t); r = A-t-(CC&0x01);
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
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
    /*TODO*///
/*TODO*////* $94 ANDA direct -**0- */
    public static opcode anda_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t); A &= t;
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $95 BITA direct -**0- */
    public static opcode bita_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t,r;
/*TODO*///	DIRBYTE(t); r = A&t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
        }
    };
    /*TODO*///
/*TODO*////* $96 LDA direct -**0- */
    public static opcode lda_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	DIRBYTE(A);
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $97 STA direct -**0- */
    public static opcode sta_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	CLR_NZV; SET_NZ8(A);
/*TODO*///	DIRECT; WM(EAD,A);
        }
    };
    /*TODO*///
/*TODO*////* $98 EORA direct -**0- */
    public static opcode eora_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t); A ^= t;
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $99 ADCA direct ***** */
    public static opcode adca_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	DIRBYTE(t); r = A+t+(CC&0x01);
/*TODO*///	CLR_HNZVC; SET_FLAGS8(A,t,r); SET_H(A,t,r);
/*TODO*///	A = r;
        }
    };
    /*TODO*///
/*TODO*////* $9a ORA direct -**0- */
    public static opcode ora_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t); A |= t;
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $9b ADDA direct ***** */
    public static opcode adda_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	DIRBYTE(t); r = A+t;
/*TODO*///	CLR_HNZVC; SET_FLAGS8(A,t,r); SET_H(A,t,r);
/*TODO*///	A = r;
        }
    };
    /*TODO*///
/*TODO*////* $9c CMPX direct -***- */
    public static opcode cmpx_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	DIRWORD(b);
/*TODO*///	d = X;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(r); SET_V16(d,b.d,r);
        }
    };
    /*TODO*///
/*TODO*////* $9c CPX direct -**** (6803) */
    public static opcode cpx_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	DIRWORD(b);
/*TODO*///	d = X;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC; SET_FLAGS16(d,b.d,r);
        }
    };
    /*TODO*///
/*TODO*////* $9d JSR direct ----- */
    public static opcode jsr_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	DIRECT;
/*TODO*///	PUSHWORD(pPC);
/*TODO*///    PC = EA;
/*TODO*///	CHANGE_PC();
        }
    };
    /*TODO*///
/*TODO*////* $9e LDS direct -**0- */
    public static opcode lds_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	DIRWORD(m6808.s);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
        }
    };
    /*TODO*///
/*TODO*////* $9f STS direct -**0- */
    public static opcode sts_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
/*TODO*///	DIRECT;
/*TODO*///	WM16(EAD,&m6808.s);
        }
    };
    /*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____Ax____
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*////* $a0 SUBA indexed ?**** */
    public static opcode suba_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	IDXBYTE(t); r = A-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
        }
    };
    /*TODO*///
/*TODO*////* $a1 CMPA indexed ?**** */
    public static opcode cmpa_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	IDXBYTE(t); r = A-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,t,r);
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
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IDXWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
        }
    };
    /*TODO*///
/*TODO*////* $a4 ANDA indexed -**0- */
    public static opcode anda_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IDXBYTE(t); A &= t;
/*TODO*///	CLR_NZV; SET_NZ8(A);
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
    /*TODO*///
/*TODO*////* $a6 LDA indexed -**0- */
    public static opcode lda_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	IDXBYTE(A);
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $a7 STA indexed -**0- */
    public static opcode sta_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	CLR_NZV; SET_NZ8(A);
/*TODO*///	INDEXED; WM(EAD,A);
        }
    };
    /*TODO*///
/*TODO*////* $a8 EORA indexed -**0- */
    public static opcode eora_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IDXBYTE(t); A ^= t;
/*TODO*///	CLR_NZV; SET_NZ8(A);
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
    /*TODO*///
/*TODO*////* $aa ORA indexed -**0- */
    public static opcode ora_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IDXBYTE(t); A |= t;
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $ab ADDA indexed ***** */
    public static opcode adda_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	IDXBYTE(t); r = A+t;
/*TODO*///	CLR_HNZVC; SET_FLAGS8(A,t,r); SET_H(A,t,r);
/*TODO*///	A = r;
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
    /*TODO*///
/*TODO*////* $ad JSR indexed ----- */
    public static opcode jsr_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	INDEXED;
/*TODO*///	PUSHWORD(pPC);
/*TODO*///    PC = EA;
/*TODO*///	CHANGE_PC();
        }
    };
    /*TODO*///
/*TODO*////* $ae LDS indexed -**0- */
    public static opcode lds_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	IDXWORD(m6808.s);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
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
    /*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____Bx____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $b0 SUBA extended ?**** */
    public static opcode suba_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	EXTBYTE(t); r = A-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
        }
    };
    /*TODO*///
/*TODO*////* $b1 CMPA extended ?**** */
    public static opcode cmpa_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	EXTBYTE(t); r = A-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(A,t,r);
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
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	EXTWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D=r;
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
    /*TODO*///
/*TODO*////* $b6 LDA extended -**0- */
    public static opcode lda_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	EXTBYTE(A);
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $b7 STA extended -**0- */
    public static opcode sta_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	CLR_NZV; SET_NZ8(A);
/*TODO*///	EXTENDED; WM(EAD,A);
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
    /*TODO*///
/*TODO*////* $ba ORA extended -**0- */
    public static opcode ora_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t); A |= t;
/*TODO*///	CLR_NZV; SET_NZ8(A);
        }
    };
    /*TODO*///
/*TODO*////* $bb ADDA extended ***** */
    public static opcode adda_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	EXTBYTE(t); r = A+t;
/*TODO*///	CLR_HNZVC; SET_FLAGS8(A,t,r); SET_H(A,t,r);
/*TODO*///	A = r;
        }
    };
    /*TODO*///
/*TODO*////* $bc CMPX extended -***- */
    public static opcode cmpx_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	EXTWORD(b);
/*TODO*///	d = X;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(r); SET_V16(d,b.d,r);
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
    /*TODO*///
/*TODO*////* $bd JSR extended ----- */
    public static opcode jsr_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	EXTENDED;
/*TODO*///	PUSHWORD(pPC);
/*TODO*///    PC = EA;
/*TODO*///	CHANGE_PC();
        }
    };
    /*TODO*///
/*TODO*////* $be LDS extended -**0- */
    public static opcode lds_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	EXTWORD(m6808.s);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
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
    /*TODO*///
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____Cx____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $c0 SUBB immediate ?**** */
    public static opcode subb_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	IMMBYTE(t); r = B-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
        }
    };
    /*TODO*///
/*TODO*////* $c1 CMPB immediate ?**** */
    public static opcode cmpb_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	IMMBYTE(t); r = B-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
        }
    };
    /*TODO*///
/*TODO*////* $c2 SBCB immediate ?**** */
    public static opcode sbcb_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	IMMBYTE(t); r = B-t-(CC&0x01);
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
        }
    };
    /*TODO*///
/*TODO*////* $c3 ADDD immediate -**** */
    public static opcode addd_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IMMWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d + b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
        }
    };
    /*TODO*///
/*TODO*////* $c4 ANDB immediate -**0- */
    public static opcode andb_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t); B &= t;
/*TODO*///	CLR_NZV; SET_NZ8(B);
        }
    };
    /*TODO*///
/*TODO*////* $c5 BITB immediate -**0- */
    public static opcode bitb_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t,r;
/*TODO*///	IMMBYTE(t); r = B&t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
        }
    };
    /*TODO*///
/*TODO*////* $c6 LDB immediate -**0- */
    public static opcode ldb_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	IMMBYTE(B);
/*TODO*///	CLR_NZV; SET_NZ8(B);
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
    /*TODO*///
/*TODO*////* $c8 EORB immediate -**0- */
    public static opcode eorb_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t); B ^= t;
/*TODO*///	CLR_NZV; SET_NZ8(B);
        }
    };
    /*TODO*///
/*TODO*////* $c9 ADCB immediate ***** */
    public static opcode adcb_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	IMMBYTE(t); r = B+t+(CC&0x01);
/*TODO*///	CLR_HNZVC; SET_FLAGS8(B,t,r); SET_H(B,t,r);
/*TODO*///	B = r;
        }
    };
    /*TODO*///
/*TODO*////* $ca ORB immediate -**0- */
    public static opcode orb_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t); B |= t;
/*TODO*///	CLR_NZV; SET_NZ8(B);
        }
    };
    /*TODO*///
/*TODO*////* $cb ADDB immediate ***** */
    public static opcode addb_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	IMMBYTE(t); r = B+t;
/*TODO*///	CLR_HNZVC; SET_FLAGS8(B,t,r); SET_H(B,t,r);
/*TODO*///	B = r;
        }
    };
    /*TODO*///
/*TODO*////* $CC LDD immediate -**0- */
    public static opcode ldd_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	IMMWORD(m6808.d);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
        }
    };
    /*TODO*///
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
    /*TODO*///
/*TODO*////* $ce LDX immediate -**0- */
    public static opcode ldx_im = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	IMMWORD(m6808.x);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
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
    /*TODO*///
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____Dx____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $d0 SUBB direct ?**** */
    public static opcode subb_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	DIRBYTE(t); r = B-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
        }
    };
    /*TODO*///
/*TODO*////* $d1 CMPB direct ?**** */
    public static opcode cmpb_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	DIRBYTE(t); r = B-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
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
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	DIRWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d + b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
        }
    };
    /*TODO*///
/*TODO*////* $d4 ANDB direct -**0- */
    public static opcode andb_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t); B &= t;
/*TODO*///	CLR_NZV; SET_NZ8(B);
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
    /*TODO*///
/*TODO*////* $d6 LDB direct -**0- */
    public static opcode ldb_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	DIRBYTE(B);
/*TODO*///	CLR_NZV; SET_NZ8(B);
        }
    };
    /*TODO*///
/*TODO*////* $d7 STB direct -**0- */
    public static opcode stb_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	CLR_NZV; SET_NZ8(B);
/*TODO*///	DIRECT; WM(EAD,B);
        }
    };
    /*TODO*///
/*TODO*////* $d8 EORB direct -**0- */
    public static opcode eorb_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t); B ^= t;
/*TODO*///	CLR_NZV; SET_NZ8(B);
        }
    };
    /*TODO*///
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
    /*TODO*///
/*TODO*////* $da ORB direct -**0- */
    public static opcode orb_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t); B |= t;
/*TODO*///	CLR_NZV; SET_NZ8(B);
        }
    };
    /*TODO*///
/*TODO*////* $db ADDB direct ***** */
    public static opcode addb_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	DIRBYTE(t); r = B+t;
/*TODO*///	CLR_HNZVC; SET_FLAGS8(B,t,r); SET_H(B,t,r);
/*TODO*///	B = r;
        }
    };
    /*TODO*///
/*TODO*////* $dc LDD direct -**0- */
    public static opcode ldd_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	DIRWORD(m6808.d);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
        }
    };
    /*TODO*///
/*TODO*////* $dd STD direct -**0- */
    public static opcode std_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	DIRECT;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
/*TODO*///	WM16(EAD,&m6808.d);
        }
    };
    /*TODO*///
/*TODO*////* $de LDX direct -**0- */
    public static opcode ldx_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	DIRWORD(m6808.x);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
        }
    };
    /*TODO*///
/*TODO*////* $dF STX direct -**0- */
    public static opcode stx_di = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
/*TODO*///	DIRECT;
/*TODO*///	WM16(EAD,&m6808.x);
        }
    };
    /*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____Ex____
/*TODO*///#endif
/*TODO*///
/*TODO*///
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
    /*TODO*///
/*TODO*////* $e1 CMPB indexed ?**** */
    public static opcode cmpb_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	IDXBYTE(t); r = B-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
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
    /*TODO*///
/*TODO*////* $e3 ADDD indexed -**** */
    public static opcode addd_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IDXWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d + b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
        }
    };
    /*TODO*///
/*TODO*////* $e4 ANDB indexed -**0- */
    public static opcode andb_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IDXBYTE(t); B &= t;
/*TODO*///	CLR_NZV; SET_NZ8(B);
        }
    };
    /*TODO*///
/*TODO*////* $e5 BITB indexed -**0- */
    public static opcode bitb_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t,r;
/*TODO*///	IDXBYTE(t); r = B&t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
        }
    };
    /*TODO*///
/*TODO*////* $e6 LDB indexed -**0- */
    public static opcode ldb_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	IDXBYTE(B);
/*TODO*///	CLR_NZV; SET_NZ8(B);
        }
    };
    /*TODO*///
/*TODO*////* $e7 STB indexed -**0- */
    public static opcode stb_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	CLR_NZV; SET_NZ8(B);
/*TODO*///	INDEXED; WM(EAD,B);
        }
    };
    /*TODO*///
/*TODO*////* $e8 EORB indexed -**0- */
    public static opcode eorb_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IDXBYTE(t); B ^= t;
/*TODO*///	CLR_NZV; SET_NZ8(B);
        }
    };
    /*TODO*///
/*TODO*////* $e9 ADCB indexed ***** */
    public static opcode adcb_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	IDXBYTE(t); r = B+t+(CC&0x01);
/*TODO*///	CLR_HNZVC; SET_FLAGS8(B,t,r); SET_H(B,t,r);
/*TODO*///	B = r;
        }
    };
    /*TODO*///
/*TODO*////* $ea ORB indexed -**0- */
    public static opcode orb_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	IDXBYTE(t); B |= t;
/*TODO*///	CLR_NZV; SET_NZ8(B);
        }
    };
    /*TODO*///
/*TODO*////* $eb ADDB indexed ***** */
    public static opcode addb_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	IDXBYTE(t); r = B+t;
/*TODO*///	CLR_HNZVC; SET_FLAGS8(B,t,r); SET_H(B,t,r);
/*TODO*///	B = r;
        }
    };
    /*TODO*///
/*TODO*////* $ec LDD indexed -**0- */
    public static opcode ldd_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	IDXWORD(m6808.d);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
        }
    };
    /*TODO*///
/*TODO*////* $ed STD indexed -**0- */
    public static opcode std_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	INDEXED;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
/*TODO*///	WM16(EAD,&m6808.d);
        }
    };
    /*TODO*///
/*TODO*////* $ee LDX indexed -**0- */
    public static opcode ldx_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	IDXWORD(m6808.x);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
        }
    };
    /*TODO*///
/*TODO*////* $ef STX indexed -**0- */
    public static opcode stx_ix = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
/*TODO*///	INDEXED;
/*TODO*///	WM16(EAD,&m6808.x);
        }
    };
    /*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____Fx____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $f0 SUBB extended ?**** */
    public static opcode subb_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	EXTBYTE(t); r = B-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
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
    /*TODO*///
/*TODO*////* $f2 SBCB extended ?**** */
    public static opcode sbcb_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16	  t,r;
/*TODO*///	EXTBYTE(t); r = B-t-(CC&0x01);
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
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
    /*TODO*///
/*TODO*////* $f4 ANDB extended -**0- */
    public static opcode andb_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t);
/*TODO*///	B &= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
        }
    };
    /*TODO*///
/*TODO*////* $f5 BITB extended -**0- */
    public static opcode bitb_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = B & t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(r);
        }
    };
    /*TODO*///
/*TODO*////* $f6 LDB extended -**0- */
    public static opcode ldb_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	EXTBYTE(B);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
        }
    };
    /*TODO*///
/*TODO*////* $f7 STB extended -**0- */
    public static opcode stb_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	CLR_NZV; SET_NZ8(B);
/*TODO*///	EXTENDED; WM(EAD,B);
        }
    };
    /*TODO*///
/*TODO*////* $f8 EORB extended -**0- */
    public static opcode eorb_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t); B ^= t;
/*TODO*///	CLR_NZV; SET_NZ8(B);
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
    /*TODO*///
/*TODO*////* $fa ORB extended -**0- */
    public static opcode orb_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t); B |= t;
/*TODO*///	CLR_NZV; SET_NZ8(B);
        }
    };
    /*TODO*///
/*TODO*////* $fb ADDB extended ***** */
    public static opcode addb_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT16 t,r;
/*TODO*///	EXTBYTE(t); r = B+t;
/*TODO*///	CLR_HNZVC; SET_FLAGS8(B,t,r); SET_H(B,t,r);
/*TODO*///	B = r;
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
    /*TODO*///
/*TODO*////* $fc ADDX extended -****    NSC8105 only.  Flags are a guess */
    public static opcode addx_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	EXTWORD(b);
/*TODO*///	d = X;
/*TODO*///	r = d + b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	X = r;
        }
    };
    /*TODO*///
/*TODO*////* $fd STD extended -**0- */
    public static opcode std_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	EXTENDED;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
/*TODO*///	WM16(EAD,&m6808.d);
        }
    };
    /*TODO*///
/*TODO*////* $fe LDX extended -**0- */
    public static opcode ldx_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	EXTWORD(m6808.x);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
        }
    };
    /*TODO*///
/*TODO*////* $ff STX extended -**0- */
    public static opcode stx_ex = new opcode() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
/*TODO*///	EXTENDED;
/*TODO*///	WM16(EAD,&m6808.x);
        }
    };

    /*TODO*///
    public static abstract interface opcode {

        public abstract void handler();
    }
}
