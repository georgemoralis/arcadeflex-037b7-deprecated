
package gr.codebb.arcadeflex.WIP.v037b7.cpu.m6809;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.fprintf;


public class m6809ops {
    
    private m6809 _cpu=null;
    
    public m6809ops(m6809 _cpu){
        this._cpu = _cpu;
    }
    
    int getDreg()//compose dreg
    {
         return _cpu._m6809.a << 8 | _cpu._m6809.b; 
    }
    
    void setDreg(int reg) //write to dreg
    { 
        _cpu._m6809.a = (char)(reg >>> 8 & 0xFF);
        _cpu._m6809.b = (char)(reg & 0xFF);
    }
/*TODO*////*
/*TODO*///
/*TODO*///HNZVC
/*TODO*///
/*TODO*///? = undefined
/*TODO*///* = affected
/*TODO*///- = unaffected
/*TODO*///0 = cleared
/*TODO*///1 = set
/*TODO*///# = CCr directly affected by instruction
/*TODO*///@ = special - carry set if bit 7 is set
/*TODO*///
/*TODO*///*/
/*TODO*///
/*TODO*///#ifdef NEW
/*TODO*///static void illegal( void )
/*TODO*///#else
/*TODO*///INLINE void illegal( void )
/*TODO*///#endif
/*TODO*///{
/*TODO*///	logerror("M6809: illegal opcode at %04x\n",PC);
/*TODO*///}
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____0x____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $00 NEG direct ?**** */
/*TODO*///INLINE void neg_di( void )
/*TODO*///{
/*TODO*///	UINT16 r,t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = -t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(0,t,r);
/*TODO*///	WM(EAD,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $01 ILLEGAL */
/*TODO*///
/*TODO*////* $02 ILLEGAL */
/*TODO*///
/*TODO*////* $03 COM direct -**01 */
/*TODO*///INLINE void com_di( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	t = ~t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(t);
/*TODO*///	SEC;
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $04 LSR direct -0*-* */
/*TODO*///INLINE void lsr_di( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	CLR_NZC;
/*TODO*///	CC |= (t & CC_C);
/*TODO*///	t >>= 1;
/*TODO*///	SET_Z8(t);
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $05 ILLEGAL */
/*TODO*///
/*TODO*////* $06 ROR direct -**-* */
/*TODO*///INLINE void ror_di( void )
/*TODO*///{
/*TODO*///	UINT8 t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r= (CC & CC_C) << 7;
/*TODO*///	CLR_NZC;
/*TODO*///	CC |= (t & CC_C);
/*TODO*///	r |= t>>1;
/*TODO*///	SET_NZ8(r);
/*TODO*///	WM(EAD,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $07 ASR direct ?**-* */
/*TODO*///INLINE void asr_di( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	CLR_NZC;
/*TODO*///	CC |= (t & CC_C);
/*TODO*///	t = (t & 0x80) | (t >> 1);
/*TODO*///	SET_NZ8(t);
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $08 ASL direct ?**** */
/*TODO*///INLINE void asl_di( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = t << 1;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(t,t,r);
/*TODO*///	WM(EAD,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $09 ROL direct -**** */
/*TODO*///INLINE void rol_di( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = (CC & CC_C) | (t << 1);
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(t,t,r);
/*TODO*///	WM(EAD,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $0A DEC direct -***- */
/*TODO*///INLINE void dec_di( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	--t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_FLAGS8D(t);
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $0B ILLEGAL */
/*TODO*///
/*TODO*////* $OC INC direct -***- */
/*TODO*///INLINE void inc_di( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	++t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_FLAGS8I(t);
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $OD TST direct -**0- */
/*TODO*///INLINE void tst_di( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(t);
/*TODO*///}
/*TODO*///
/*TODO*////* $0E JMP direct ----- */
/*TODO*///INLINE void jmp_di( void )
/*TODO*///{
/*TODO*///	DIRECT;
/*TODO*///	PCD = EAD;
/*TODO*///	CHANGE_PC;
/*TODO*///}
/*TODO*///
/*TODO*////* $0F CLR direct -0100 */
/*TODO*///INLINE void clr_di( void )
/*TODO*///{
/*TODO*///	DIRECT;
/*TODO*///	WM(EAD,0);
/*TODO*///	CLR_NZVC;
/*TODO*///	SEZ;
/*TODO*///}
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____1x____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $10 FLAG */
/*TODO*///
/*TODO*////* $11 FLAG */
/*TODO*///
/*TODO*////* $12 NOP inherent ----- */
/*TODO*///INLINE void nop( void )
/*TODO*///{
/*TODO*///	;
/*TODO*///}
/*TODO*///
/*TODO*////* $13 SYNC inherent ----- */
/*TODO*///INLINE void sync( void )
/*TODO*///{
/*TODO*///	/* SYNC stops processing instructions until an interrupt request happens. */
/*TODO*///	/* This doesn't require the corresponding interrupt to be enabled: if it */
/*TODO*///	/* is disabled, execution continues with the next instruction. */
/*TODO*///	m6809.int_state |= M6809_SYNC;	 /* HJB 990227 */
/*TODO*///	CHECK_IRQ_LINES;
/*TODO*///	/* if M6809_SYNC has not been cleared by CHECK_IRQ_LINES,
/*TODO*///	 * stop execution until the interrupt lines change. */
/*TODO*///	if( m6809.int_state & M6809_SYNC )
/*TODO*///		if (m6809_ICount > 0) m6809_ICount = 0;
/*TODO*///}
/*TODO*///
/*TODO*////* $14 ILLEGAL */
/*TODO*///
/*TODO*////* $15 ILLEGAL */
/*TODO*///
/*TODO*////* $16 LBRA relative ----- */
/*TODO*///INLINE void lbra( void )
/*TODO*///{
/*TODO*///	IMMWORD(ea);
/*TODO*///	PC += EA;
/*TODO*///	CHANGE_PC;
/*TODO*///
/*TODO*///	if ( EA == 0xfffd )  /* EHC 980508 speed up busy loop */
/*TODO*///		if ( m6809_ICount > 0)
/*TODO*///			m6809_ICount = 0;
/*TODO*///}
/*TODO*///
/*TODO*////* $17 LBSR relative ----- */
/*TODO*///INLINE void lbsr( void )
/*TODO*///{
/*TODO*///	IMMWORD(ea);
/*TODO*///	PUSHWORD(pPC);
/*TODO*///	PC += EA;
/*TODO*///	CHANGE_PC;
/*TODO*///}
/*TODO*///
/*TODO*////* $18 ILLEGAL */
/*TODO*///
/*TODO*///#if 1
/*TODO*////* $19 DAA inherent (A) -**0* */
/*TODO*///INLINE void daa( void )
/*TODO*///{
/*TODO*///	UINT8 msn, lsn;
/*TODO*///	UINT16 t, cf = 0;
/*TODO*///	msn = A & 0xf0; lsn = A & 0x0f;
/*TODO*///	if( lsn>0x09 || CC & CC_H) cf |= 0x06;
/*TODO*///	if( msn>0x80 && lsn>0x09 ) cf |= 0x60;
/*TODO*///	if( msn>0x90 || CC & CC_C) cf |= 0x60;
/*TODO*///	t = cf + A;
/*TODO*///	CLR_NZV; /* keep carry from previous operation */
/*TODO*///	SET_NZ8((UINT8)t); SET_C8(t);
/*TODO*///	A = t;
/*TODO*///}
/*TODO*///#else
/*TODO*////* $19 DAA inherent (A) -**0* */
/*TODO*///INLINE void daa( void )
/*TODO*///{
/*TODO*///	UINT16 t;
/*TODO*///	t = A;
/*TODO*///	if ((CC & CC_H) != 0) t+=0x06;
/*TODO*///	if ((t&0x0f)>9) t+=0x06;		/* ASG -- this code is broken! $66+$99=$FF . DAA should = $65, we get $05! */
/*TODO*///	if ((CC & CC_C) != 0) t+=0x60;
/*TODO*///	if ((t&0xf0)>0x90) t+=0x60;
/*TODO*///	if ((t & 0x100) != 0) SEC;
/*TODO*///	A = t;
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*////* $1A ORCC immediate ##### */
/*TODO*///INLINE void orcc( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	CC |= t;
/*TODO*///	CHECK_IRQ_LINES;	/* HJB 990116 */
/*TODO*///}
/*TODO*///
/*TODO*////* $1B ILLEGAL */
/*TODO*///
/*TODO*////* $1C ANDCC immediate ##### */
/*TODO*///INLINE void andcc( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	CC &= t;
/*TODO*///	CHECK_IRQ_LINES;	/* HJB 990116 */
/*TODO*///}
/*TODO*///
/*TODO*////* $1D SEX inherent -**0- */
/*TODO*///INLINE void sex( void )
/*TODO*///{
/*TODO*///	UINT16 t;
/*TODO*///	t = SIGNED(B);
/*TODO*///	D = t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(t);
/*TODO*///}
/*TODO*///
/*TODO*////* $1E EXG inherent ----- */
/*TODO*///INLINE void exg( void )
/*TODO*///{
/*TODO*///	UINT16 t1,t2;
/*TODO*///	UINT8 tb;
/*TODO*///
/*TODO*///	IMMBYTE(tb);
/*TODO*///	if( (tb^(tb>>4)) & 0x08 )	/* HJB 990225: mixed 8/16 bit case? */
/*TODO*///	{
/*TODO*///		/* transfer $ff to both registers */
/*TODO*///		t1 = t2 = 0xff;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		switch(tb>>4) {
/*TODO*///			case  0: t1 = D;  break;
/*TODO*///			case  1: t1 = X;  break;
/*TODO*///			case  2: t1 = Y;  break;
/*TODO*///			case  3: t1 = U;  break;
/*TODO*///			case  4: t1 = S;  break;
/*TODO*///			case  5: t1 = PC; break;
/*TODO*///			case  8: t1 = A;  break;
/*TODO*///			case  9: t1 = B;  break;
/*TODO*///			case 10: t1 = CC; break;
/*TODO*///			case 11: t1 = DP; break;
/*TODO*///			default: t1 = 0xff;
/*TODO*///		}
/*TODO*///		switch(tb&15) {
/*TODO*///			case  0: t2 = D;  break;
/*TODO*///			case  1: t2 = X;  break;
/*TODO*///			case  2: t2 = Y;  break;
/*TODO*///			case  3: t2 = U;  break;
/*TODO*///			case  4: t2 = S;  break;
/*TODO*///			case  5: t2 = PC; break;
/*TODO*///			case  8: t2 = A;  break;
/*TODO*///			case  9: t2 = B;  break;
/*TODO*///			case 10: t2 = CC; break;
/*TODO*///			case 11: t2 = DP; break;
/*TODO*///			default: t2 = 0xff;
/*TODO*///        }
/*TODO*///	}
/*TODO*///	switch(tb>>4) {
/*TODO*///		case  0: D = t2;  break;
/*TODO*///		case  1: X = t2;  break;
/*TODO*///		case  2: Y = t2;  break;
/*TODO*///		case  3: U = t2;  break;
/*TODO*///		case  4: S = t2;  break;
/*TODO*///		case  5: PC = t2; CHANGE_PC; break;
/*TODO*///		case  8: A = t2;  break;
/*TODO*///		case  9: B = t2;  break;
/*TODO*///		case 10: CC = t2; break;
/*TODO*///		case 11: DP = t2; break;
/*TODO*///	}
/*TODO*///	switch(tb&15) {
/*TODO*///		case  0: D = t1;  break;
/*TODO*///		case  1: X = t1;  break;
/*TODO*///		case  2: Y = t1;  break;
/*TODO*///		case  3: U = t1;  break;
/*TODO*///		case  4: S = t1;  break;
/*TODO*///		case  5: PC = t1; CHANGE_PC; break;
/*TODO*///		case  8: A = t1;  break;
/*TODO*///		case  9: B = t1;  break;
/*TODO*///		case 10: CC = t1; break;
/*TODO*///		case 11: DP = t1; break;
/*TODO*///	}
/*TODO*///}

    /* $1F TFR inherent ----- */
    public void tfr()
    {
            /*UINT8*/int tb;
            /*UINT16*/ int t;

            tb=_cpu.IMMBYTE();
            if(( (tb^(tb>>4)) & 0x08 )!=0)	/* HJB 990225: mixed 8/16 bit case? */
            {
                    /* transfer $ff to register */
                    t = 0xff;
        }
            else
            {
                    switch(tb>>4) {
                            case  0: t = getDreg();  break;
                            case  1: t = _cpu._m6809.x;  break;
                            case  2: t = _cpu._m6809.y;  break;
                            case  3: t = _cpu._m6809.u;  break;
                            case  4: t = _cpu._m6809.s;  break;
                            case  5: t = _cpu._m6809.pc; break;
                            case  8: t = _cpu._m6809.a;  break;
                            case  9: t = _cpu._m6809.b;  break;
                            case 10: t = _cpu._m6809.cc; break;
                            case 11: t = _cpu._m6809.dp; break;
                            default: t = 0xff;
            }
            }
            switch(tb&15) {
                    case  0: setDreg(t);  break;
                    case  1: _cpu._m6809.x = (char) t;  break;
                    case  2: _cpu._m6809.y = (char) t;  break;
                    case  3: _cpu._m6809.u = (char) t;  break;
                    case  4: _cpu._m6809.s = (char) t;  break;
                    case  5: _cpu._m6809.pc = (char) t; _cpu.CHANGE_PC(); break;
                    case  8: _cpu._m6809.a = (char) t;  break;
                    case  9: _cpu._m6809.b = (char) t;  break;
                    case 10: _cpu._m6809.cc = (char) t; break;
                    case 11: _cpu._m6809.dp = (char) t; break;
        }
    }

/*TODO*///#if macintosh
/*TODO*///#pragma mark ____2x____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $20 BRA relative ----- */
/*TODO*///INLINE void bra( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	PC += SIGNED(t);
/*TODO*///    CHANGE_PC;
/*TODO*///	/* JB 970823 - speed up busy loops */
/*TODO*///	if( t == 0xfe )
/*TODO*///		if( m6809_ICount > 0 ) m6809_ICount = 0;
/*TODO*///}
/*TODO*///
/*TODO*////* $21 BRN relative ----- */
/*TODO*///INLINE void brn( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///}
/*TODO*///
/*TODO*////* $1021 LBRN relative ----- */
/*TODO*///INLINE void lbrn( void )
/*TODO*///{
/*TODO*///	IMMWORD(ea);
/*TODO*///}
/*TODO*///
/*TODO*////* $22 BHI relative ----- */
/*TODO*///INLINE void bhi( void )
/*TODO*///{
/*TODO*///	BRANCH( !(CC & (CC_Z|CC_C)) );
/*TODO*///}
/*TODO*///
/*TODO*////* $1022 LBHI relative ----- */
/*TODO*///INLINE void lbhi( void )
/*TODO*///{
/*TODO*///	LBRANCH( !(CC & (CC_Z|CC_C)) );
/*TODO*///}
/*TODO*///
/*TODO*////* $23 BLS relative ----- */
/*TODO*///INLINE void bls( void )
/*TODO*///{
/*TODO*///	BRANCH( (CC & (CC_Z|CC_C)) );
/*TODO*///}
/*TODO*///
/*TODO*////* $1023 LBLS relative ----- */
/*TODO*///INLINE void lbls( void )
/*TODO*///{
/*TODO*///	LBRANCH( (CC&(CC_Z|CC_C)) );
/*TODO*///}
/*TODO*///
/*TODO*////* $24 BCC relative ----- */
/*TODO*///INLINE void bcc( void )
/*TODO*///{
/*TODO*///	BRANCH( !(CC&CC_C) );
/*TODO*///}
/*TODO*///
/*TODO*////* $1024 LBCC relative ----- */
/*TODO*///INLINE void lbcc( void )
/*TODO*///{
/*TODO*///	LBRANCH( !(CC&CC_C) );
/*TODO*///}
    
    /* $25 BCS relative ----- */
    public void bcs()
    {
    	_cpu.BRANCH( (_cpu._m6809.cc & _cpu.CC_C)!=0 );
    }
    
/*TODO*////* $1025 LBCS relative ----- */
/*TODO*///INLINE void lbcs( void )
/*TODO*///{
/*TODO*///	LBRANCH( (CC&CC_C) );
/*TODO*///}

    /* $26 BNE relative ----- */
    public void bne()
    {
            _cpu.BRANCH( (_cpu._m6809.cc&_cpu.CC_Z)==0 );
    }

    /* $1026 LBNE relative ----- */
    public void lbne()
    {
    	_cpu.LBRANCH( (_cpu._m6809.cc&_cpu.CC_Z)==0 );
    }

    /* $27 BEQ relative ----- */
    public void beq()
    {
            _cpu.BRANCH( (_cpu._m6809.cc&_cpu.CC_Z) != 0 );            
    }

/*TODO*////* $1027 LBEQ relative ----- */
/*TODO*///INLINE void lbeq( void )
/*TODO*///{
/*TODO*///	LBRANCH( (CC&CC_Z) );
/*TODO*///}
/*TODO*///
/*TODO*////* $28 BVC relative ----- */
/*TODO*///INLINE void bvc( void )
/*TODO*///{
/*TODO*///	BRANCH( !(CC&CC_V) );
/*TODO*///}
/*TODO*///
/*TODO*////* $1028 LBVC relative ----- */
/*TODO*///INLINE void lbvc( void )
/*TODO*///{
/*TODO*///	LBRANCH( !(CC&CC_V) );
/*TODO*///}
/*TODO*///
/*TODO*////* $29 BVS relative ----- */
/*TODO*///INLINE void bvs( void )
/*TODO*///{
/*TODO*///	BRANCH( (CC&CC_V) );
/*TODO*///}
/*TODO*///
/*TODO*////* $1029 LBVS relative ----- */
/*TODO*///INLINE void lbvs( void )
/*TODO*///{
/*TODO*///	LBRANCH( (CC&CC_V) );
/*TODO*///}
/*TODO*///
/*TODO*////* $2A BPL relative ----- */
/*TODO*///INLINE void bpl( void )
/*TODO*///{
/*TODO*///	BRANCH( !(CC&CC_N) );
/*TODO*///}
/*TODO*///
/*TODO*////* $102A LBPL relative ----- */
/*TODO*///INLINE void lbpl( void )
/*TODO*///{
/*TODO*///	LBRANCH( !(CC&CC_N) );
/*TODO*///}
/*TODO*///
/*TODO*////* $2B BMI relative ----- */
/*TODO*///INLINE void bmi( void )
/*TODO*///{
/*TODO*///	BRANCH( (CC&CC_N) );
/*TODO*///}
/*TODO*///
/*TODO*////* $102B LBMI relative ----- */
/*TODO*///INLINE void lbmi( void )
/*TODO*///{
/*TODO*///	LBRANCH( (CC&CC_N) );
/*TODO*///}
/*TODO*///
/*TODO*////* $2C BGE relative ----- */
/*TODO*///INLINE void bge( void )
/*TODO*///{
/*TODO*///	BRANCH( !NXORV );
/*TODO*///}
/*TODO*///
/*TODO*////* $102C LBGE relative ----- */
/*TODO*///INLINE void lbge( void )
/*TODO*///{
/*TODO*///	LBRANCH( !NXORV );
/*TODO*///}
/*TODO*///
/*TODO*////* $2D BLT relative ----- */
/*TODO*///INLINE void blt( void )
/*TODO*///{
/*TODO*///	BRANCH( NXORV );
/*TODO*///}
/*TODO*///
/*TODO*////* $102D LBLT relative ----- */
/*TODO*///INLINE void lblt( void )
/*TODO*///{
/*TODO*///	LBRANCH( NXORV );
/*TODO*///}
/*TODO*///
/*TODO*////* $2E BGT relative ----- */
/*TODO*///INLINE void bgt( void )
/*TODO*///{
/*TODO*///	BRANCH( !(NXORV || (CC&CC_Z)) );
/*TODO*///}
/*TODO*///
/*TODO*////* $102E LBGT relative ----- */
/*TODO*///INLINE void lbgt( void )
/*TODO*///{
/*TODO*///	LBRANCH( !(NXORV || (CC&CC_Z)) );
/*TODO*///}
/*TODO*///
/*TODO*////* $2F BLE relative ----- */
/*TODO*///INLINE void ble( void )
/*TODO*///{
/*TODO*///	BRANCH( (NXORV || (CC&CC_Z)) );
/*TODO*///}
/*TODO*///
/*TODO*////* $102F LBLE relative ----- */
/*TODO*///INLINE void lble( void )
/*TODO*///{
/*TODO*///	LBRANCH( (NXORV || (CC&CC_Z)) );
/*TODO*///}
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____3x____
/*TODO*///#endif

    /* $30 LEAX indexed --*-- */
    public void leax()
    {
            _cpu.fetch_effective_address();
            _cpu._m6809.x = (char) _cpu.ea;
            _cpu.CLR_Z();
            _cpu.SET_Z(_cpu._m6809.x);            
    }

/*TODO*////* $31 LEAY indexed --*-- */
/*TODO*///INLINE void leay( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    Y = EA;
/*TODO*///	CLR_Z;
/*TODO*///	SET_Z(Y);
/*TODO*///}
/*TODO*///
/*TODO*////* $32 LEAS indexed ----- */
/*TODO*///INLINE void leas( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    S = EA;
/*TODO*///	m6809.int_state |= M6809_LDS;
/*TODO*///}
/*TODO*///
/*TODO*////* $33 LEAU indexed ----- */
/*TODO*///INLINE void leau( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    U = EA;
/*TODO*///}
/*TODO*///
/*TODO*////* $34 PSHS inherent ----- */
/*TODO*///INLINE void pshs( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	if ((t & 0x80) != 0) { PUSHWORD(pPC); m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x40) != 0) { PUSHWORD(pU);  m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x20) != 0) { PUSHWORD(pY);  m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x10) != 0) { PUSHWORD(pX);  m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x08) != 0) { PUSHBYTE(DP);  m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x04) != 0) { PUSHBYTE(B);   m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x02) != 0) { PUSHBYTE(A);   m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x01) != 0) { PUSHBYTE(CC);  m6809_ICount -= 1; }
/*TODO*///}
/*TODO*///
/*TODO*////* 35 PULS inherent ----- */
/*TODO*///INLINE void puls( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	if ((t & 0x01) != 0) { PULLBYTE(CC); m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x02) != 0) { PULLBYTE(A);  m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x04) != 0) { PULLBYTE(B);  m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x08) != 0) { PULLBYTE(DP); m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x10) != 0) { PULLWORD(XD); m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x20) != 0) { PULLWORD(YD); m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x40) != 0) { PULLWORD(UD); m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x80) != 0) { PULLWORD(PCD); CHANGE_PC; m6809_ICount -= 2; }
/*TODO*///
/*TODO*///	/* HJB 990225: moved check after all PULLs */
/*TODO*///	if ((t & 0x01) != 0) { CHECK_IRQ_LINES; }
/*TODO*///}
/*TODO*///
/*TODO*////* $36 PSHU inherent ----- */
/*TODO*///INLINE void pshu( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	if ((t & 0x80) != 0) { PSHUWORD(pPC); m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x40) != 0) { PSHUWORD(pS);  m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x20) != 0) { PSHUWORD(pY);  m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x10) != 0) { PSHUWORD(pX);  m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x08) != 0) { PSHUBYTE(DP);  m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x04) != 0) { PSHUBYTE(B);   m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x02) != 0) { PSHUBYTE(A);   m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x01) != 0) { PSHUBYTE(CC);  m6809_ICount -= 1; }
/*TODO*///}
/*TODO*///
/*TODO*////* 37 PULU inherent ----- */
/*TODO*///INLINE void pulu( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	if ((t & 0x01) != 0) { PULUBYTE(CC); m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x02) != 0) { PULUBYTE(A);  m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x04) != 0) { PULUBYTE(B);  m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x08) != 0) { PULUBYTE(DP); m6809_ICount -= 1; }
/*TODO*///	if ((t & 0x10) != 0) { PULUWORD(XD); m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x20) != 0) { PULUWORD(YD); m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x40) != 0) { PULUWORD(SD); m6809_ICount -= 2; }
/*TODO*///	if ((t & 0x80) != 0) { PULUWORD(PCD); CHANGE_PC; m6809_ICount -= 2; }
/*TODO*///
/*TODO*///	/* HJB 990225: moved check after all PULLs */
/*TODO*///	if ((t & 0x01) != 0) { CHECK_IRQ_LINES; }
/*TODO*///}
/*TODO*///
/*TODO*////* $38 ILLEGAL */
/*TODO*///
/*TODO*////* $39 RTS inherent ----- */
/*TODO*///INLINE void rts( void )
/*TODO*///{
/*TODO*///	PULLWORD(PCD);
/*TODO*///	CHANGE_PC;
/*TODO*///}
/*TODO*///
/*TODO*////* $3A ABX inherent ----- */
/*TODO*///INLINE void abx( void )
/*TODO*///{
/*TODO*///	X += B;
/*TODO*///}
/*TODO*///
/*TODO*////* $3B RTI inherent ##### */
/*TODO*///INLINE void rti( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	PULLBYTE(CC);
/*TODO*///	t = CC & CC_E;		/* HJB 990225: entire state saved? */
/*TODO*///	if (t != 0)
/*TODO*///	{
/*TODO*///        m6809_ICount -= 9;
/*TODO*///		PULLBYTE(A);
/*TODO*///		PULLBYTE(B);
/*TODO*///		PULLBYTE(DP);
/*TODO*///		PULLWORD(XD);
/*TODO*///		PULLWORD(YD);
/*TODO*///		PULLWORD(UD);
/*TODO*///	}
/*TODO*///	PULLWORD(PCD);
/*TODO*///	CHANGE_PC;
/*TODO*///	CHECK_IRQ_LINES;	/* HJB 990116 */
/*TODO*///}
/*TODO*///
/*TODO*////* $3C CWAI inherent ----1 */
/*TODO*///INLINE void cwai( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	CC &= t;
/*TODO*///	/*
/*TODO*///     * CWAI stacks the entire machine state on the hardware stack,
/*TODO*///     * then waits for an interrupt; when the interrupt is taken
/*TODO*///     * later, the state is *not* saved again after CWAI.
/*TODO*///     */
/*TODO*///	CC |= CC_E; 		/* HJB 990225: save entire state */
/*TODO*///	PUSHWORD(pPC);
/*TODO*///	PUSHWORD(pU);
/*TODO*///	PUSHWORD(pY);
/*TODO*///	PUSHWORD(pX);
/*TODO*///	PUSHBYTE(DP);
/*TODO*///	PUSHBYTE(B);
/*TODO*///	PUSHBYTE(A);
/*TODO*///	PUSHBYTE(CC);
/*TODO*///	m6809.int_state |= M6809_CWAI;	 /* HJB 990228 */
/*TODO*///    CHECK_IRQ_LINES;    /* HJB 990116 */
/*TODO*///	if( m6809.int_state & M6809_CWAI )
/*TODO*///		if( m6809_ICount > 0 )
/*TODO*///			m6809_ICount = 0;
/*TODO*///}

    /* $3D MUL inherent --*-@ */
    public void mul()
    {                      
        int t;
    	t = ((_cpu._m6809.a&0xff) * (_cpu._m6809.b&0xff)) & 0xFFFF;
    	_cpu.CLR_ZC(); 
        _cpu.SET_Z16(t); 
        if((t&0x80)!=0) _cpu.SEC();
    	setDreg(t);
    }

/*TODO*////* $3E ILLEGAL */
/*TODO*///
/*TODO*////* $3F SWI (SWI2 SWI3) absolute indirect ----- */
/*TODO*///INLINE void swi( void )
/*TODO*///{
/*TODO*///	CC |= CC_E; 			/* HJB 980225: save entire state */
/*TODO*///	PUSHWORD(pPC);
/*TODO*///	PUSHWORD(pU);
/*TODO*///	PUSHWORD(pY);
/*TODO*///	PUSHWORD(pX);
/*TODO*///	PUSHBYTE(DP);
/*TODO*///	PUSHBYTE(B);
/*TODO*///	PUSHBYTE(A);
/*TODO*///	PUSHBYTE(CC);
/*TODO*///	CC |= CC_IF | CC_II;	/* inhibit FIRQ and IRQ */
/*TODO*///	PCD=RM16(0xfffa);
/*TODO*///	CHANGE_PC;
/*TODO*///}
/*TODO*///
/*TODO*////* $103F SWI2 absolute indirect ----- */
/*TODO*///INLINE void swi2( void )
/*TODO*///{
/*TODO*///	CC |= CC_E; 			/* HJB 980225: save entire state */
/*TODO*///	PUSHWORD(pPC);
/*TODO*///	PUSHWORD(pU);
/*TODO*///	PUSHWORD(pY);
/*TODO*///	PUSHWORD(pX);
/*TODO*///	PUSHBYTE(DP);
/*TODO*///	PUSHBYTE(B);
/*TODO*///	PUSHBYTE(A);
/*TODO*///    PUSHBYTE(CC);
/*TODO*///	PCD = RM16(0xfff4);
/*TODO*///	CHANGE_PC;
/*TODO*///}
/*TODO*///
/*TODO*////* $113F SWI3 absolute indirect ----- */
/*TODO*///INLINE void swi3( void )
/*TODO*///{
/*TODO*///	CC |= CC_E; 			/* HJB 980225: save entire state */
/*TODO*///	PUSHWORD(pPC);
/*TODO*///	PUSHWORD(pU);
/*TODO*///	PUSHWORD(pY);
/*TODO*///	PUSHWORD(pX);
/*TODO*///	PUSHBYTE(DP);
/*TODO*///	PUSHBYTE(B);
/*TODO*///	PUSHBYTE(A);
/*TODO*///    PUSHBYTE(CC);
/*TODO*///	PCD = RM16(0xfff2);
/*TODO*///	CHANGE_PC;
/*TODO*///}
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____4x____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $40 NEGA inherent ?**** */
/*TODO*///INLINE void nega( void )
/*TODO*///{
/*TODO*///	UINT16 r;
/*TODO*///	r = -A;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(0,A,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $41 ILLEGAL */
/*TODO*///
/*TODO*////* $42 ILLEGAL */
/*TODO*///
/*TODO*////* $43 COMA inherent -**01 */
/*TODO*///INLINE void coma( void )
/*TODO*///{
/*TODO*///	A = ~A;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///	SEC;
/*TODO*///}
/*TODO*///
/*TODO*////* $44 LSRA inherent -0*-* */
/*TODO*///INLINE void lsra( void )
/*TODO*///{
/*TODO*///	CLR_NZC;
/*TODO*///	CC |= (A & CC_C);
/*TODO*///	A >>= 1;
/*TODO*///	SET_Z8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $45 ILLEGAL */
/*TODO*///
/*TODO*////* $46 RORA inherent -**-* */
/*TODO*///INLINE void rora( void )
/*TODO*///{
/*TODO*///	UINT8 r;
/*TODO*///	r = (CC & CC_C) << 7;
/*TODO*///	CLR_NZC;
/*TODO*///	CC |= (A & CC_C);
/*TODO*///	r |= A >> 1;
/*TODO*///	SET_NZ8(r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $47 ASRA inherent ?**-* */
/*TODO*///INLINE void asra( void )
/*TODO*///{
/*TODO*///	CLR_NZC;
/*TODO*///	CC |= (A & CC_C);
/*TODO*///	A = (A & 0x80) | (A >> 1);
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $48 ASLA inherent ?**** */
/*TODO*///INLINE void asla( void )
/*TODO*///{
/*TODO*///	UINT16 r;
/*TODO*///	r = A << 1;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(A,A,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $49 ROLA inherent -**** */
/*TODO*///INLINE void rola( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	t = A;
/*TODO*///	r = (CC & CC_C) | (t<<1);
/*TODO*///	CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $4A DECA inherent -***- */
/*TODO*///INLINE void deca( void )
/*TODO*///{
/*TODO*///	--A;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_FLAGS8D(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $4B ILLEGAL */
/*TODO*///
/*TODO*////* $4C INCA inherent -***- */
/*TODO*///INLINE void inca( void )
/*TODO*///{
/*TODO*///	++A;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_FLAGS8I(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $4D TSTA inherent -**0- */
/*TODO*///INLINE void tsta( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $4E ILLEGAL */

    /* $4F CLRA inherent -0100 */
    public void clra()
    {
        _cpu._m6809.a = 0;
        _cpu.CLR_NZVC(); _cpu.SEZ();
             
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d clra :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }

/*TODO*///#if macintosh
/*TODO*///#pragma mark ____5x____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $50 NEGB inherent ?**** */
/*TODO*///INLINE void negb( void )
/*TODO*///{
/*TODO*///	UINT16 r;
/*TODO*///	r = -B;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(0,B,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $51 ILLEGAL */
/*TODO*///
/*TODO*////* $52 ILLEGAL */

    /* $53 COMB inherent -**01 */
    public void comb()
    {
            _cpu._m6809.b = (char) ~(_cpu._m6809.b & 0xFF);
            _cpu.CLR_NZV();
            _cpu.SET_NZ8(_cpu._m6809.b);
            _cpu.SEC();                        
    }

/*TODO*////* $54 LSRB inherent -0*-* */
/*TODO*///INLINE void lsrb( void )
/*TODO*///{
/*TODO*///	CLR_NZC;
/*TODO*///	CC |= (B & CC_C);
/*TODO*///	B >>= 1;
/*TODO*///	SET_Z8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $55 ILLEGAL */
/*TODO*///
/*TODO*////* $56 RORB inherent -**-* */
/*TODO*///INLINE void rorb( void )
/*TODO*///{
/*TODO*///	UINT8 r;
/*TODO*///	r = (CC & CC_C) << 7;
/*TODO*///	CLR_NZC;
/*TODO*///	CC |= (B & CC_C);
/*TODO*///	r |= B >> 1;
/*TODO*///	SET_NZ8(r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $57 ASRB inherent ?**-* */
/*TODO*///INLINE void asrb( void )
/*TODO*///{
/*TODO*///	CLR_NZC;
/*TODO*///	CC |= (B & CC_C);
/*TODO*///	B= (B & 0x80) | (B >> 1);
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $58 ASLB inherent ?**** */
/*TODO*///INLINE void aslb( void )
/*TODO*///{
/*TODO*///	UINT16 r;
/*TODO*///	r = B << 1;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(B,B,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $59 ROLB inherent -**** */
/*TODO*///INLINE void rolb( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	t = B;
/*TODO*///	r = CC & CC_C;
/*TODO*///	r |= t << 1;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(t,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $5A DECB inherent -***- */
/*TODO*///INLINE void decb( void )
/*TODO*///{
/*TODO*///	--B;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_FLAGS8D(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $5B ILLEGAL */
/*TODO*///
/*TODO*////* $5C INCB inherent -***- */
/*TODO*///INLINE void incb( void )
/*TODO*///{
/*TODO*///	++B;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_FLAGS8I(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $5D TSTB inherent -**0- */
/*TODO*///INLINE void tstb( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $5E ILLEGAL */
/*TODO*///
/*TODO*////* $5F CLRB inherent -0100 */
/*TODO*///INLINE void clrb( void )
/*TODO*///{
/*TODO*///	B = 0;
/*TODO*///	CLR_NZVC; SEZ;
/*TODO*///}
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____6x____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $60 NEG indexed ?**** */
/*TODO*///INLINE void neg_ix( void )
/*TODO*///{
/*TODO*///	UINT16 r,t;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD);
/*TODO*///	r=-t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(0,t,r);
/*TODO*///	WM(EAD,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $61 ILLEGAL */
/*TODO*///
/*TODO*////* $62 ILLEGAL */
/*TODO*///
/*TODO*////* $63 COM indexed -**01 */
/*TODO*///INLINE void com_ix( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = ~RM(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(t);
/*TODO*///	SEC;
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $64 LSR indexed -0*-* */
/*TODO*///INLINE void lsr_ix( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	fetch_effective_address();
/*TODO*///	t=RM(EAD);
/*TODO*///	CLR_NZC;
/*TODO*///	CC |= (t & CC_C);
/*TODO*///	t>>=1; SET_Z8(t);
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $65 ILLEGAL */
/*TODO*///
/*TODO*////* $66 ROR indexed -**-* */
/*TODO*///INLINE void ror_ix( void )
/*TODO*///{
/*TODO*///	UINT8 t,r;
/*TODO*///	fetch_effective_address();
/*TODO*///	t=RM(EAD);
/*TODO*///	r = (CC & CC_C) << 7;
/*TODO*///	CLR_NZC;
/*TODO*///	CC |= (t & CC_C);
/*TODO*///	r |= t>>1; SET_NZ8(r);
/*TODO*///	WM(EAD,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $67 ASR indexed ?**-* */
/*TODO*///INLINE void asr_ix( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	fetch_effective_address();
/*TODO*///	t=RM(EAD);
/*TODO*///	CLR_NZC;
/*TODO*///	CC |= (t & CC_C);
/*TODO*///	t=(t&0x80)|(t>>1);
/*TODO*///	SET_NZ8(t);
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $68 ASL indexed ?**** */
/*TODO*///INLINE void asl_ix( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	fetch_effective_address();
/*TODO*///	t=RM(EAD);
/*TODO*///	r = t << 1;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(t,t,r);
/*TODO*///	WM(EAD,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $69 ROL indexed -**** */
/*TODO*///INLINE void rol_ix( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	fetch_effective_address();
/*TODO*///	t=RM(EAD);
/*TODO*///	r = CC & CC_C;
/*TODO*///	r |= t << 1;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(t,t,r);
/*TODO*///	WM(EAD,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $6A DEC indexed -***- */
/*TODO*///INLINE void dec_ix( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD) - 1;
/*TODO*///	CLR_NZV; SET_FLAGS8D(t);
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $6B ILLEGAL */
/*TODO*///
/*TODO*////* $6C INC indexed -***- */
/*TODO*///INLINE void inc_ix( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD) + 1;
/*TODO*///	CLR_NZV; SET_FLAGS8I(t);
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $6D TST indexed -**0- */
/*TODO*///INLINE void tst_ix( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(t);
/*TODO*///}
/*TODO*///
/*TODO*////* $6E JMP indexed ----- */
/*TODO*///INLINE void jmp_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///	PCD = EAD;
/*TODO*///	CHANGE_PC;
/*TODO*///}
/*TODO*///
/*TODO*////* $6F CLR indexed -0100 */
/*TODO*///INLINE void clr_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    WM(EAD,0);
/*TODO*///	CLR_NZVC; SEZ;
/*TODO*///}
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____7x____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $70 NEG extended ?**** */
/*TODO*///INLINE void neg_ex( void )
/*TODO*///{
/*TODO*///	UINT16 r,t;
/*TODO*///	EXTBYTE(t); r=-t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(0,t,r);
/*TODO*///	WM(EAD,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $71 ILLEGAL */
/*TODO*///
/*TODO*////* $72 ILLEGAL */
/*TODO*///
/*TODO*////* $73 COM extended -**01 */
/*TODO*///INLINE void com_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t); t = ~t;
/*TODO*///	CLR_NZV; SET_NZ8(t); SEC;
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $74 LSR extended -0*-* */
/*TODO*///INLINE void lsr_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t); CLR_NZC; CC |= (t & CC_C);
/*TODO*///	t>>=1; SET_Z8(t);
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $75 ILLEGAL */
/*TODO*///
/*TODO*////* $76 ROR extended -**-* */
/*TODO*///INLINE void ror_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t,r;
/*TODO*///	EXTBYTE(t); r=(CC & CC_C) << 7;
/*TODO*///	CLR_NZC; CC |= (t & CC_C);
/*TODO*///	r |= t>>1; SET_NZ8(r);
/*TODO*///	WM(EAD,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $77 ASR extended ?**-* */
/*TODO*///INLINE void asr_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t); CLR_NZC; CC |= (t & CC_C);
/*TODO*///	t=(t&0x80)|(t>>1);
/*TODO*///	SET_NZ8(t);
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $78 ASL extended ?**** */
/*TODO*///INLINE void asl_ex( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	EXTBYTE(t); r=t<<1;
/*TODO*///	CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///	WM(EAD,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $79 ROL extended -**** */
/*TODO*///INLINE void rol_ex( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	EXTBYTE(t); r = (CC & CC_C) | (t << 1);
/*TODO*///	CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///	WM(EAD,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $7A DEC extended -***- */
/*TODO*///INLINE void dec_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t); --t;
/*TODO*///	CLR_NZV; SET_FLAGS8D(t);
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $7B ILLEGAL */
/*TODO*///
/*TODO*////* $7C INC extended -***- */
/*TODO*///INLINE void inc_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t); ++t;
/*TODO*///	CLR_NZV; SET_FLAGS8I(t);
/*TODO*///	WM(EAD,t);
/*TODO*///}
/*TODO*///
/*TODO*////* $7D TST extended -**0- */
/*TODO*///INLINE void tst_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t); CLR_NZV; SET_NZ8(t);
/*TODO*///}
/*TODO*///
/*TODO*////* $7E JMP extended ----- */
/*TODO*///INLINE void jmp_ex( void )
/*TODO*///{
/*TODO*///	EXTENDED;
/*TODO*///	PCD = EAD;
/*TODO*///	CHANGE_PC;
/*TODO*///}
/*TODO*///
/*TODO*////* $7F CLR extended -0100 */
/*TODO*///INLINE void clr_ex( void )
/*TODO*///{
/*TODO*///	EXTENDED;
/*TODO*///	WM(EAD,0);
/*TODO*///	CLR_NZVC; SEZ;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____8x____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $80 SUBA immediate ?**** */
/*TODO*///INLINE void suba_im( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	r = A - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
    
    /* $81 CMPA immediate ?**** */
    public void cmpa_im()
    {
    	int	  t,r;
    	t=_cpu.IMMBYTE();
    	r = _cpu._m6809.a - t;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    }
    
/*TODO*////* $82 SBCA immediate ?**** */
/*TODO*///INLINE void sbca_im( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	r = A - t - (CC & CC_C);
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $83 SUBD (CMPD CMPU) immediate -**** */
/*TODO*///INLINE void subd_im( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IMMWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $1083 CMPD immediate -**** */
/*TODO*///INLINE void cmpd_im( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IMMWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $1183 CMPU immediate -**** */
/*TODO*///INLINE void cmpu_im( void )
/*TODO*///{
/*TODO*///	UINT32 r, d;
/*TODO*///	PAIR b;
/*TODO*///	IMMWORD(b);
/*TODO*///	d = U;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $84 ANDA immediate -**0- */
/*TODO*///INLINE void anda_im( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	A &= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $85 BITA immediate -**0- */
/*TODO*///INLINE void bita_im( void )
/*TODO*///{
/*TODO*///	UINT8 t,r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	r = A & t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(r);
/*TODO*///}

    /* $86 LDA immediate -**0- */
    public void lda_im()
    {
            _cpu._m6809.a=_cpu.IMMBYTE();
            _cpu.CLR_NZV();
            _cpu.SET_NZ8(_cpu._m6809.a);
    }

/*TODO*////* is this a legal instruction? */
/*TODO*////* $87 STA immediate -**0- */
/*TODO*///INLINE void sta_im( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///	IMM8;
/*TODO*///	WM(EAD,A);
/*TODO*///}
/*TODO*///
/*TODO*////* $88 EORA immediate -**0- */
/*TODO*///INLINE void eora_im( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	A ^= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $89 ADCA immediate ***** */
/*TODO*///INLINE void adca_im( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	r = A + t + (CC & CC_C);
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	SET_H(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $8A ORA immediate -**0- */
/*TODO*///INLINE void ora_im( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	A |= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
    
    /* $8B ADDA immediate ***** */
    public void adda_im()
    {
    	int t,r;
    	t=_cpu.IMMBYTE();
    	r = _cpu._m6809.a + t;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu.SET_H(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char) r;
    }
    
    /* $8C CMPX (CMPY CMPS) immediate -**** */
    public void cmpx_im()
    {
    	int r,d;
    	int b;
    	b=_cpu.IMMWORD();
    	d = _cpu._m6809.x;
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
    }
    
/*TODO*////* $108C CMPY immediate -**** */
/*TODO*///INLINE void cmpy_im( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IMMWORD(b);
/*TODO*///	d = Y;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $118C CMPS immediate -**** */
/*TODO*///INLINE void cmps_im( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IMMWORD(b);
/*TODO*///	d = S;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $8D BSR ----- */
/*TODO*///INLINE void bsr( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	PUSHWORD(pPC);
/*TODO*///	PC += SIGNED(t);
/*TODO*///	CHANGE_PC;
/*TODO*///}

    /* $8E LDX (LDY) immediate -**0- */
    public void ldx_im()
    {
            _cpu._m6809.x=_cpu.IMMWORD();
            _cpu.CLR_NZV();
            _cpu.SET_NZ16(_cpu._m6809.x);            
    }

/*TODO*////* $108E LDY immediate -**0- */
/*TODO*///INLINE void ldy_im( void )
/*TODO*///{
/*TODO*///	IMMWORD(pY);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(Y);
/*TODO*///}
/*TODO*///
/*TODO*////* is this a legal instruction? */
/*TODO*////* $8F STX (STY) immediate -**0- */
/*TODO*///INLINE void stx_im( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
/*TODO*///	IMM16;
/*TODO*///	WM16(EAD,&pX);
/*TODO*///}
/*TODO*///
/*TODO*////* is this a legal instruction? */
/*TODO*////* $108F STY immediate -**0- */
/*TODO*///INLINE void sty_im( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(Y);
/*TODO*///	IMM16;
/*TODO*///	WM16(EAD,&pY);
/*TODO*///}
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____9x____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $90 SUBA direct ?**** */
/*TODO*///INLINE void suba_di( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = A - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $91 CMPA direct ?**** */
/*TODO*///INLINE void cmpa_di( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = A - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $92 SBCA direct ?**** */
/*TODO*///INLINE void sbca_di( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = A - t - (CC & CC_C);
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $93 SUBD (CMPD CMPU) direct -**** */
/*TODO*///INLINE void subd_di( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	DIRWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $1093 CMPD direct -**** */
/*TODO*///INLINE void cmpd_di( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	DIRWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $1193 CMPU direct -**** */
/*TODO*///INLINE void cmpu_di( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	DIRWORD(b);
/*TODO*///	d = U;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(U,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $94 ANDA direct -**0- */
/*TODO*///INLINE void anda_di( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	A &= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $95 BITA direct -**0- */
/*TODO*///INLINE void bita_di( void )
/*TODO*///{
/*TODO*///	UINT8 t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = A & t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(r);
/*TODO*///}
/*TODO*///
/*TODO*////* $96 LDA direct -**0- */
/*TODO*///INLINE void lda_di( void )
/*TODO*///{
/*TODO*///	DIRBYTE(A);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $97 STA direct -**0- */
/*TODO*///INLINE void sta_di( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///	DIRECT;
/*TODO*///	WM(EAD,A);
/*TODO*///}
/*TODO*///
/*TODO*////* $98 EORA direct -**0- */
/*TODO*///INLINE void eora_di( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	A ^= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $99 ADCA direct ***** */
/*TODO*///INLINE void adca_di( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = A + t + (CC & CC_C);
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	SET_H(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $9A ORA direct -**0- */
/*TODO*///INLINE void ora_di( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	A |= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $9B ADDA direct ***** */
/*TODO*///INLINE void adda_di( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = A + t;
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	SET_H(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $9C CMPX (CMPY CMPS) direct -**** */
/*TODO*///INLINE void cmpx_di( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	DIRWORD(b);
/*TODO*///	d = X;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $109C CMPY direct -**** */
/*TODO*///INLINE void cmpy_di( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	DIRWORD(b);
/*TODO*///	d = Y;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $119C CMPS direct -**** */
/*TODO*///INLINE void cmps_di( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	DIRWORD(b);
/*TODO*///	d = S;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $9D JSR direct ----- */
/*TODO*///INLINE void jsr_di( void )
/*TODO*///{
/*TODO*///	DIRECT;
/*TODO*///	PUSHWORD(pPC);
/*TODO*///	PCD = EAD;
/*TODO*///	CHANGE_PC;
/*TODO*///}
/*TODO*///
/*TODO*////* $9E LDX (LDY) direct -**0- */
/*TODO*///INLINE void ldx_di( void )
/*TODO*///{
/*TODO*///	DIRWORD(pX);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
/*TODO*///}
/*TODO*///
/*TODO*////* $109E LDY direct -**0- */
/*TODO*///INLINE void ldy_di( void )
/*TODO*///{
/*TODO*///	DIRWORD(pY);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(Y);
/*TODO*///}
/*TODO*///
/*TODO*////* $9F STX (STY) direct -**0- */
/*TODO*///INLINE void stx_di( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
/*TODO*///	DIRECT;
/*TODO*///	WM16(EAD,&pX);
/*TODO*///}
/*TODO*///
/*TODO*////* $109F STY direct -**0- */
/*TODO*///INLINE void sty_di( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(Y);
/*TODO*///	DIRECT;
/*TODO*///	WM16(EAD,&pY);
/*TODO*///}
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____Ax____
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*////* $a0 SUBA indexed ?**** */
/*TODO*///INLINE void suba_ix( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD);
/*TODO*///	r = A - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
/*TODO*///}

    /* $a1 CMPA indexed ?**** */
    public void cmpa_ix()
    {
            int t,r;
            _cpu.fetch_effective_address();
            t = _cpu.RM(_cpu.ea);
            r = _cpu._m6809.a - t;
            _cpu.CLR_NZVC();
            _cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    }

/*TODO*////* $a2 SBCA indexed ?**** */
/*TODO*///INLINE void sbca_ix( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD);
/*TODO*///	r = A - t - (CC & CC_C);
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $a3 SUBD (CMPD CMPU) indexed -**** */
/*TODO*///INLINE void subd_ix( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	fetch_effective_address();
/*TODO*///    b.d=RM16(EAD);
/*TODO*///	d = D;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $10a3 CMPD indexed -**** */
/*TODO*///INLINE void cmpd_ix( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	fetch_effective_address();
/*TODO*///    b.d=RM16(EAD);
/*TODO*///	d = D;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $11a3 CMPU indexed -**** */
/*TODO*///INLINE void cmpu_ix( void )
/*TODO*///{
/*TODO*///	UINT32 r;
/*TODO*///	PAIR b;
/*TODO*///	fetch_effective_address();
/*TODO*///    b.d=RM16(EAD);
/*TODO*///	r = U - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(U,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $a4 ANDA indexed -**0- */
/*TODO*///INLINE void anda_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///	A &= RM(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $a5 BITA indexed -**0- */
/*TODO*///INLINE void bita_ix( void )
/*TODO*///{
/*TODO*///	UINT8 r;
/*TODO*///	fetch_effective_address();
/*TODO*///	r = A & RM(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(r);
/*TODO*///}
    
    /* $a6 LDA indexed -**0- */
    public void lda_ix()
    {
    	_cpu.fetch_effective_address();
    	_cpu._m6809.a = _cpu.RM(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
    }

    /* $a7 STA indexed -**0- */
    public void sta_ix()
    {
            _cpu.fetch_effective_address();
            _cpu.CLR_NZV();
            _cpu.SET_NZ8(_cpu._m6809.a);
            _cpu.WM(_cpu.ea,_cpu._m6809.a);
    }

/*TODO*////* $a8 EORA indexed -**0- */
/*TODO*///INLINE void eora_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///	A ^= RM(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $a9 ADCA indexed ***** */
/*TODO*///INLINE void adca_ix( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD);
/*TODO*///	r = A + t + (CC & CC_C);
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	SET_H(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $aA ORA indexed -**0- */
/*TODO*///INLINE void ora_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///	A |= RM(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $aB ADDA indexed ***** */
/*TODO*///INLINE void adda_ix( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD);
/*TODO*///	r = A + t;
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	SET_H(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $aC CMPX (CMPY CMPS) indexed -**** */
/*TODO*///INLINE void cmpx_ix( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	fetch_effective_address();
/*TODO*///    b.d=RM16(EAD);
/*TODO*///	d = X;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $10aC CMPY indexed -**** */
/*TODO*///INLINE void cmpy_ix( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	fetch_effective_address();
/*TODO*///    b.d=RM16(EAD);
/*TODO*///	d = Y;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $11aC CMPS indexed -**** */
/*TODO*///INLINE void cmps_ix( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	fetch_effective_address();
/*TODO*///    b.d=RM16(EAD);
/*TODO*///	d = S;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $aD JSR indexed ----- */
/*TODO*///INLINE void jsr_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    PUSHWORD(pPC);
/*TODO*///	PCD = EAD;
/*TODO*///	CHANGE_PC;
/*TODO*///}
/*TODO*///
/*TODO*////* $aE LDX (LDY) indexed -**0- */
/*TODO*///INLINE void ldx_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    X=RM16(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
/*TODO*///}
/*TODO*///
/*TODO*////* $10aE LDY indexed -**0- */
/*TODO*///INLINE void ldy_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    Y=RM16(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(Y);
/*TODO*///}
/*TODO*///
/*TODO*////* $aF STX (STY) indexed -**0- */
/*TODO*///INLINE void stx_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    CLR_NZV;
/*TODO*///	SET_NZ16(X);
/*TODO*///	WM16(EAD,&pX);
/*TODO*///}
/*TODO*///
/*TODO*////* $10aF STY indexed -**0- */
/*TODO*///INLINE void sty_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    CLR_NZV;
/*TODO*///	SET_NZ16(Y);
/*TODO*///	WM16(EAD,&pY);
/*TODO*///}
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____Bx____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $b0 SUBA extended ?**** */
/*TODO*///INLINE void suba_ex( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = A - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $b1 CMPA extended ?**** */
/*TODO*///INLINE void cmpa_ex( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = A - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $b2 SBCA extended ?**** */
/*TODO*///INLINE void sbca_ex( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = A - t - (CC & CC_C);
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $b3 SUBD (CMPD CMPU) extended -**** */
/*TODO*///INLINE void subd_ex( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	EXTWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $10b3 CMPD extended -**** */
/*TODO*///INLINE void cmpd_ex( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	EXTWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $11b3 CMPU extended -**** */
/*TODO*///INLINE void cmpu_ex( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	EXTWORD(b);
/*TODO*///	d = U;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $b4 ANDA extended -**0- */
/*TODO*///INLINE void anda_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t);
/*TODO*///	A &= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $b5 BITA extended -**0- */
/*TODO*///INLINE void bita_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = A & t;
/*TODO*///	CLR_NZV; SET_NZ8(r);
/*TODO*///}
/*TODO*///
/*TODO*////* $b6 LDA extended -**0- */
/*TODO*///INLINE void lda_ex( void )
/*TODO*///{
/*TODO*///	EXTBYTE(A);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}

    /* $b7 STA extended -**0- */
    public void sta_ex()
    {
            _cpu.CLR_NZV();
            _cpu.SET_NZ8(_cpu._m6809.a);
            _cpu.EXTENDED();
            _cpu.WM(_cpu.ea,_cpu._m6809.a);
    }

/*TODO*////* $b8 EORA extended -**0- */
/*TODO*///INLINE void eora_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t);
/*TODO*///	A ^= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $b9 ADCA extended ***** */
/*TODO*///INLINE void adca_ex( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = A + t + (CC & CC_C);
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	SET_H(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $bA ORA extended -**0- */
/*TODO*///INLINE void ora_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t);
/*TODO*///	A |= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(A);
/*TODO*///}
/*TODO*///
/*TODO*////* $bB ADDA extended ***** */
/*TODO*///INLINE void adda_ex( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = A + t;
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(A,t,r);
/*TODO*///	SET_H(A,t,r);
/*TODO*///	A = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $bC CMPX (CMPY CMPS) extended -**** */
/*TODO*///INLINE void cmpx_ex( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	EXTWORD(b);
/*TODO*///	d = X;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $10bC CMPY extended -**** */
/*TODO*///INLINE void cmpy_ex( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	EXTWORD(b);
/*TODO*///	d = Y;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $11bC CMPS extended -**** */
/*TODO*///INLINE void cmps_ex( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	EXTWORD(b);
/*TODO*///	d = S;
/*TODO*///	r = d - b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $bD JSR extended ----- */
/*TODO*///INLINE void jsr_ex( void )
/*TODO*///{
/*TODO*///	EXTENDED;
/*TODO*///	PUSHWORD(pPC);
/*TODO*///	PCD = EAD;
/*TODO*///	CHANGE_PC;
/*TODO*///}
/*TODO*///
/*TODO*////* $bE LDX (LDY) extended -**0- */
/*TODO*///INLINE void ldx_ex( void )
/*TODO*///{
/*TODO*///	EXTWORD(pX);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
/*TODO*///}
/*TODO*///
/*TODO*////* $10bE LDY extended -**0- */
/*TODO*///INLINE void ldy_ex( void )
/*TODO*///{
/*TODO*///	EXTWORD(pY);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(Y);
/*TODO*///}
/*TODO*///
/*TODO*////* $bF STX (STY) extended -**0- */
/*TODO*///INLINE void stx_ex( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(X);
/*TODO*///	EXTENDED;
/*TODO*///	WM16(EAD,&pX);
/*TODO*///}
/*TODO*///
/*TODO*////* $10bF STY extended -**0- */
/*TODO*///INLINE void sty_ex( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(Y);
/*TODO*///	EXTENDED;
/*TODO*///	WM16(EAD,&pY);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____Cx____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $c0 SUBB immediate ?**** */
/*TODO*///INLINE void subb_im( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	r = B - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $c1 CMPB immediate ?**** */
/*TODO*///INLINE void cmpb_im( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	r = B - t;
/*TODO*///	CLR_NZVC; SET_FLAGS8(B,t,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $c2 SBCB immediate ?**** */
/*TODO*///INLINE void sbcb_im( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	r = B - t - (CC & CC_C);
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $c3 ADDD immediate -**** */
/*TODO*///INLINE void addd_im( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	IMMWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d + b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $c4 ANDB immediate -**0- */
/*TODO*///INLINE void andb_im( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	B &= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}

    /* $c5 BITB immediate -**0- */
    public void bitb_im()
    {
            int t,r;
            t =_cpu.IMMBYTE();
            r = (_cpu._m6809.b & t) & 0xFF;
            _cpu.CLR_NZV();
            _cpu.SET_NZ8(r);
    }

    /* $c6 LDB immediate -**0- */
    public void ldb_im()
    {
            _cpu._m6809.b = _cpu.IMMBYTE();
            _cpu.CLR_NZV();
            _cpu.SET_NZ8(_cpu._m6809.b);
    }

/*TODO*////* is this a legal instruction? */
/*TODO*////* $c7 STB immediate -**0- */
/*TODO*///INLINE void stb_im( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///	IMM8;
/*TODO*///	WM(EAD,B);
/*TODO*///}
/*TODO*///
/*TODO*////* $c8 EORB immediate -**0- */
/*TODO*///INLINE void eorb_im( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	B ^= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $c9 ADCB immediate ***** */
/*TODO*///INLINE void adcb_im( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	r = B + t + (CC & CC_C);
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	SET_H(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $cA ORB immediate -**0- */
/*TODO*///INLINE void orb_im( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	IMMBYTE(t);
/*TODO*///	B |= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $cB ADDB immediate ***** */
/*TODO*///INLINE void addb_im( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	IMMBYTE(t);
/*TODO*///	r = B + t;
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	SET_H(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $cC LDD immediate -**0- */
/*TODO*///INLINE void ldd_im( void )
/*TODO*///{
/*TODO*///	IMMWORD(pD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
/*TODO*///}
/*TODO*///
/*TODO*////* is this a legal instruction? */
/*TODO*////* $cD STD immediate -**0- */
/*TODO*///INLINE void std_im( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
/*TODO*///    IMM16;
/*TODO*///	WM16(EAD,&pD);
/*TODO*///}
    
    /* $cE LDU (LDS) immediate -**0- */
    public void ldu_im()
    {
    	_cpu._m6809.u = _cpu.IMMWORD();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.u);
    }

    /* $10cE LDS immediate -**0- */
    public void lds_im()
    {
            _cpu._m6809.s=_cpu.IMMWORD();
            _cpu.CLR_NZV();
            _cpu.SET_NZ16(_cpu._m6809.s);
            _cpu._m6809.int_state |= _cpu.M6809_LDS;
    }

/*TODO*////* is this a legal instruction? */
/*TODO*////* $cF STU (STS) immediate -**0- */
/*TODO*///INLINE void stu_im( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(U);
/*TODO*///    IMM16;
/*TODO*///	WM16(EAD,&pU);
/*TODO*///}
/*TODO*///
/*TODO*////* is this a legal instruction? */
/*TODO*////* $10cF STS immediate -**0- */
/*TODO*///INLINE void sts_im( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
/*TODO*///    IMM16;
/*TODO*///	WM16(EAD,&pS);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____Dx____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $d0 SUBB direct ?**** */
/*TODO*///INLINE void subb_di( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = B - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $d1 CMPB direct ?**** */
/*TODO*///INLINE void cmpb_di( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = B - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $d2 SBCB direct ?**** */
/*TODO*///INLINE void sbcb_di( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = B - t - (CC & CC_C);
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $d3 ADDD direct -**** */
/*TODO*///INLINE void addd_di( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	DIRWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d + b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $d4 ANDB direct -**0- */
/*TODO*///INLINE void andb_di( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	B &= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $d5 BITB direct -**0- */
/*TODO*///INLINE void bitb_di( void )
/*TODO*///{
/*TODO*///	UINT8 t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = B & t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(r);
/*TODO*///}
/*TODO*///
/*TODO*////* $d6 LDB direct -**0- */
/*TODO*///INLINE void ldb_di( void )
/*TODO*///{
/*TODO*///	DIRBYTE(B);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $d7 STB direct -**0- */
/*TODO*///INLINE void stb_di( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///	DIRECT;
/*TODO*///	WM(EAD,B);
/*TODO*///}
/*TODO*///
/*TODO*////* $d8 EORB direct -**0- */
/*TODO*///INLINE void eorb_di( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	B ^= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $d9 ADCB direct ***** */
/*TODO*///INLINE void adcb_di( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = B + t + (CC & CC_C);
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	SET_H(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $dA ORB direct -**0- */
/*TODO*///INLINE void orb_di( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	DIRBYTE(t);
/*TODO*///	B |= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $dB ADDB direct ***** */
/*TODO*///INLINE void addb_di( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	DIRBYTE(t);
/*TODO*///	r = B + t;
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	SET_H(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $dC LDD direct -**0- */
/*TODO*///INLINE void ldd_di( void )
/*TODO*///{
/*TODO*///	DIRWORD(pD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
/*TODO*///}
/*TODO*///
/*TODO*////* $dD STD direct -**0- */
/*TODO*///INLINE void std_di( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
/*TODO*///    DIRECT;
/*TODO*///	WM16(EAD,&pD);
/*TODO*///}
/*TODO*///
/*TODO*////* $dE LDU (LDS) direct -**0- */
/*TODO*///INLINE void ldu_di( void )
/*TODO*///{
/*TODO*///	DIRWORD(pU);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(U);
/*TODO*///}
/*TODO*///
/*TODO*////* $10dE LDS direct -**0- */
/*TODO*///INLINE void lds_di( void )
/*TODO*///{
/*TODO*///	DIRWORD(pS);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
/*TODO*///	m6809.int_state |= M6809_LDS;
/*TODO*///}
/*TODO*///
/*TODO*////* $dF STU (STS) direct -**0- */
/*TODO*///INLINE void stu_di( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(U);
/*TODO*///	DIRECT;
/*TODO*///	WM16(EAD,&pU);
/*TODO*///}
/*TODO*///
/*TODO*////* $10dF STS direct -**0- */
/*TODO*///INLINE void sts_di( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
/*TODO*///	DIRECT;
/*TODO*///	WM16(EAD,&pS);
/*TODO*///}
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____Ex____
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*////* $e0 SUBB indexed ?**** */
/*TODO*///INLINE void subb_ix( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD);
/*TODO*///	r = B - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $e1 CMPB indexed ?**** */
/*TODO*///INLINE void cmpb_ix( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD);
/*TODO*///	r = B - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $e2 SBCB indexed ?**** */
/*TODO*///INLINE void sbcb_ix( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD);
/*TODO*///	r = B - t - (CC & CC_C);
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $e3 ADDD indexed -**** */
/*TODO*///INLINE void addd_ix( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///    PAIR b;
/*TODO*///    fetch_effective_address();
/*TODO*///	b.d=RM16(EAD);
/*TODO*///	d = D;
/*TODO*///	r = d + b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $e4 ANDB indexed -**0- */
/*TODO*///INLINE void andb_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///	B &= RM(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $e5 BITB indexed -**0- */
/*TODO*///INLINE void bitb_ix( void )
/*TODO*///{
/*TODO*///	UINT8 r;
/*TODO*///	fetch_effective_address();
/*TODO*///	r = B & RM(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(r);
/*TODO*///}
/*TODO*///
/*TODO*////* $e6 LDB indexed -**0- */
/*TODO*///INLINE void ldb_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///	B = RM(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
    
    /* $e7 STB indexed -**0- */
    public void stb_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
    	_cpu.WM(_cpu.ea,_cpu._m6809.b);
    }
    
/*TODO*////* $e8 EORB indexed -**0- */
/*TODO*///INLINE void eorb_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///	B ^= RM(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $e9 ADCB indexed ***** */
/*TODO*///INLINE void adcb_ix( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD);
/*TODO*///	r = B + t + (CC & CC_C);
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	SET_H(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $eA ORB indexed -**0- */
/*TODO*///INLINE void orb_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///	B |= RM(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $eB ADDB indexed ***** */
/*TODO*///INLINE void addb_ix( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	fetch_effective_address();
/*TODO*///	t = RM(EAD);
/*TODO*///	r = B + t;
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	SET_H(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $eC LDD indexed -**0- */
/*TODO*///INLINE void ldd_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    D=RM16(EAD);
/*TODO*///	CLR_NZV; SET_NZ16(D);
/*TODO*///}
/*TODO*///
/*TODO*////* $eD STD indexed -**0- */
/*TODO*///INLINE void std_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    CLR_NZV;
/*TODO*///	SET_NZ16(D);
/*TODO*///	WM16(EAD,&pD);
/*TODO*///}
/*TODO*///
/*TODO*////* $eE LDU (LDS) indexed -**0- */
/*TODO*///INLINE void ldu_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    U=RM16(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(U);
/*TODO*///}
/*TODO*///
/*TODO*////* $10eE LDS indexed -**0- */
/*TODO*///INLINE void lds_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    S=RM16(EAD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
/*TODO*///	m6809.int_state |= M6809_LDS;
/*TODO*///}
    
    /* $eF STU (STS) indexed -**0- */
    public void stu_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.u);
    	_cpu.WM16(_cpu.ea,_cpu._m6809.u);
    }
    
/*TODO*////* $10eF STS indexed -**0- */
/*TODO*///INLINE void sts_ix( void )
/*TODO*///{
/*TODO*///	fetch_effective_address();
/*TODO*///    CLR_NZV;
/*TODO*///	SET_NZ16(S);
/*TODO*///	WM16(EAD,&pS);
/*TODO*///}
/*TODO*///
/*TODO*///#if macintosh
/*TODO*///#pragma mark ____Fx____
/*TODO*///#endif
/*TODO*///
/*TODO*////* $f0 SUBB extended ?**** */
/*TODO*///INLINE void subb_ex( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = B - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $f1 CMPB extended ?**** */
/*TODO*///INLINE void cmpb_ex( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = B - t;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///}
/*TODO*///
/*TODO*////* $f2 SBCB extended ?**** */
/*TODO*///INLINE void sbcb_ex( void )
/*TODO*///{
/*TODO*///	UINT16	  t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = B - t - (CC & CC_C);
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $f3 ADDD extended -**** */
/*TODO*///INLINE void addd_ex( void )
/*TODO*///{
/*TODO*///	UINT32 r,d;
/*TODO*///	PAIR b;
/*TODO*///	EXTWORD(b);
/*TODO*///	d = D;
/*TODO*///	r = d + b.d;
/*TODO*///	CLR_NZVC;
/*TODO*///	SET_FLAGS16(d,b.d,r);
/*TODO*///	D = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $f4 ANDB extended -**0- */
/*TODO*///INLINE void andb_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t);
/*TODO*///	B &= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $f5 BITB extended -**0- */
/*TODO*///INLINE void bitb_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = B & t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(r);
/*TODO*///}

    /* $f6 LDB extended -**0- */
    public void ldb_ex()
    {
            _cpu._m6809.b=_cpu.EXTBYTE();
            _cpu.CLR_NZV();
            _cpu.SET_NZ8(_cpu._m6809.b);            
    }

    /* $f7 STB extended -**0- */
    public void stb_ex()
    {
            _cpu.CLR_NZV();
            _cpu.SET_NZ8(_cpu._m6809.b);
            _cpu.EXTENDED();
            _cpu.WM(_cpu.ea,_cpu._m6809.b);
    }

/*TODO*////* $f8 EORB extended -**0- */
/*TODO*///INLINE void eorb_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t);
/*TODO*///	B ^= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $f9 ADCB extended ***** */
/*TODO*///INLINE void adcb_ex( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = B + t + (CC & CC_C);
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	SET_H(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $fA ORB extended -**0- */
/*TODO*///INLINE void orb_ex( void )
/*TODO*///{
/*TODO*///	UINT8 t;
/*TODO*///	EXTBYTE(t);
/*TODO*///	B |= t;
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ8(B);
/*TODO*///}
/*TODO*///
/*TODO*////* $fB ADDB extended ***** */
/*TODO*///INLINE void addb_ex( void )
/*TODO*///{
/*TODO*///	UINT16 t,r;
/*TODO*///	EXTBYTE(t);
/*TODO*///	r = B + t;
/*TODO*///	CLR_HNZVC;
/*TODO*///	SET_FLAGS8(B,t,r);
/*TODO*///	SET_H(B,t,r);
/*TODO*///	B = r;
/*TODO*///}
/*TODO*///
/*TODO*////* $fC LDD extended -**0- */
/*TODO*///INLINE void ldd_ex( void )
/*TODO*///{
/*TODO*///	EXTWORD(pD);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
/*TODO*///}
/*TODO*///
/*TODO*////* $fD STD extended -**0- */
/*TODO*///INLINE void std_ex( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(D);
/*TODO*///    EXTENDED;
/*TODO*///	WM16(EAD,&pD);
/*TODO*///}
/*TODO*///
/*TODO*////* $fE LDU (LDS) extended -**0- */
/*TODO*///INLINE void ldu_ex( void )
/*TODO*///{
/*TODO*///	EXTWORD(pU);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(U);
/*TODO*///}
/*TODO*///
/*TODO*////* $10fE LDS extended -**0- */
/*TODO*///INLINE void lds_ex( void )
/*TODO*///{
/*TODO*///	EXTWORD(pS);
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
/*TODO*///	m6809.int_state |= M6809_LDS;
/*TODO*///}
/*TODO*///
/*TODO*////* $fF STU (STS) extended -**0- */
/*TODO*///INLINE void stu_ex( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(U);
/*TODO*///	EXTENDED;
/*TODO*///	WM16(EAD,&pU);
/*TODO*///}
/*TODO*///
/*TODO*////* $10fF STS extended -**0- */
/*TODO*///INLINE void sts_ex( void )
/*TODO*///{
/*TODO*///	CLR_NZV;
/*TODO*///	SET_NZ16(S);
/*TODO*///	EXTENDED;
/*TODO*///	WM16(EAD,&pS);
/*TODO*///}

/* $10xx opcodes */
public void pref10()
{
	int ireg2 = _cpu.ROP(_cpu._m6809.pc) &0xFF;
        _cpu._m6809.pc++;
	
        switch( ireg2 )
	{
/*TODO*///		case 0x21: lbrn();		m6809_ICount-=5;	break;
/*TODO*///		case 0x22: lbhi();		m6809_ICount-=5;	break;
/*TODO*///		case 0x23: lbls();		m6809_ICount-=5;	break;
/*TODO*///		case 0x24: lbcc();		m6809_ICount-=5;	break;
/*TODO*///		case 0x25: lbcs();		m6809_ICount-=5;	break;
		case 0x26: lbne();		_cpu.m6809_ICount[0]-=5;	break;
/*TODO*///		case 0x27: lbeq();		m6809_ICount-=5;	break;
/*TODO*///		case 0x28: lbvc();		m6809_ICount-=5;	break;
/*TODO*///		case 0x29: lbvs();		m6809_ICount-=5;	break;
/*TODO*///		case 0x2a: lbpl();		m6809_ICount-=5;	break;
/*TODO*///		case 0x2b: lbmi();		m6809_ICount-=5;	break;
/*TODO*///		case 0x2c: lbge();		m6809_ICount-=5;	break;
/*TODO*///		case 0x2d: lblt();		m6809_ICount-=5;	break;
/*TODO*///		case 0x2e: lbgt();		m6809_ICount-=5;	break;
/*TODO*///		case 0x2f: lble();		m6809_ICount-=5;	break;
/*TODO*///
/*TODO*///		case 0x3f: swi2();		m6809_ICount-=20;	break;
/*TODO*///
/*TODO*///		case 0x83: cmpd_im();	m6809_ICount-=5;	break;
/*TODO*///		case 0x8c: cmpy_im();	m6809_ICount-=5;	break;
/*TODO*///		case 0x8e: ldy_im();	m6809_ICount-=4;	break;
/*TODO*///		case 0x8f: sty_im();	m6809_ICount-=4;	break;
/*TODO*///
/*TODO*///		case 0x93: cmpd_di();	m6809_ICount-=7;	break;
/*TODO*///		case 0x9c: cmpy_di();	m6809_ICount-=7;	break;
/*TODO*///		case 0x9e: ldy_di();	m6809_ICount-=6;	break;
/*TODO*///		case 0x9f: sty_di();	m6809_ICount-=6;	break;
/*TODO*///
/*TODO*///		case 0xa3: cmpd_ix();	m6809_ICount-=7;	break;
/*TODO*///		case 0xac: cmpy_ix();	m6809_ICount-=7;	break;
/*TODO*///		case 0xae: ldy_ix();	m6809_ICount-=6;	break;
/*TODO*///		case 0xaf: sty_ix();	m6809_ICount-=6;	break;
/*TODO*///
/*TODO*///		case 0xb3: cmpd_ex();	m6809_ICount-=8;	break;
/*TODO*///		case 0xbc: cmpy_ex();	m6809_ICount-=8;	break;
/*TODO*///		case 0xbe: ldy_ex();	m6809_ICount-=7;	break;
/*TODO*///		case 0xbf: sty_ex();	m6809_ICount-=7;	break;

		case 0xce: lds_im();	_cpu.m6809_ICount[0]-=4;	break;
/*TODO*///		case 0xcf: sts_im();	m6809_ICount-=4;	break;

/*TODO*///		case 0xde: lds_di();	m6809_ICount-=6;	break;
/*TODO*///		case 0xdf: sts_di();	m6809_ICount-=6;	break;
/*TODO*///
/*TODO*///		case 0xee: lds_ix();	m6809_ICount-=6;	break;
/*TODO*///		case 0xef: sts_ix();	m6809_ICount-=6;	break;
/*TODO*///
/*TODO*///		case 0xfe: lds_ex();	m6809_ICount-=7;	break;
/*TODO*///		case 0xff: sts_ex();	m6809_ICount-=7;	break;

/*TODO*///		default:   illegal();						break;
            default:
                throw new UnsupportedOperationException("pref10: Opcode "+ireg2+" not implemented!");
	}
}

/*TODO*////* $11xx opcodes */
/*TODO*///INLINE void pref11( void )
/*TODO*///{
/*TODO*///	UINT8 ireg2 = ROP(PCD);
/*TODO*///	PC++;
/*TODO*///	switch( ireg2 )
/*TODO*///	{
/*TODO*///		case 0x3f: swi3();		m6809_ICount-=20;	break;
/*TODO*///
/*TODO*///		case 0x83: cmpu_im();	m6809_ICount-=5;	break;
/*TODO*///		case 0x8c: cmps_im();	m6809_ICount-=5;	break;
/*TODO*///
/*TODO*///		case 0x93: cmpu_di();	m6809_ICount-=7;	break;
/*TODO*///		case 0x9c: cmps_di();	m6809_ICount-=7;	break;
/*TODO*///
/*TODO*///		case 0xa3: cmpu_ix();	m6809_ICount-=7;	break;
/*TODO*///		case 0xac: cmps_ix();	m6809_ICount-=7;	break;
/*TODO*///
/*TODO*///		case 0xb3: cmpu_ex();	m6809_ICount-=8;	break;
/*TODO*///		case 0xbc: cmps_ex();	m6809_ICount-=8;	break;
/*TODO*///
/*TODO*///		default:   illegal();						break;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
    
}
