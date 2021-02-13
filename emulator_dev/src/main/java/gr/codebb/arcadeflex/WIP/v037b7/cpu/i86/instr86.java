/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.I86H.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;

public class instr86 {

    public static abstract interface InstructionPtr {

        public abstract void handler();
    }

    /*TODO*///#if !defined(V20) && !defined(I186)
/*TODO*///static void PREFIX86(_interrupt)(unsigned int_num)
/*TODO*///{
/*TODO*///	unsigned dest_seg, dest_off;
/*TODO*///	WORD ip = I.pc - I.base[CS];
/*TODO*///
/*TODO*///	if (int_num == -1)
/*TODO*///		int_num = (*I.irq_callback)(0);
/*TODO*///
/*TODO*///#ifdef I286
/*TODO*///	if (PM) {
/*TODO*///		i286_interrupt_descriptor(int_num);
/*TODO*///	} else {
/*TODO*///#endif
/*TODO*///		dest_off = ReadWord(int_num*4);
/*TODO*///		dest_seg = ReadWord(int_num*4+2);
/*TODO*///
/*TODO*///		PREFIX(_pushf());
/*TODO*///		I.TF = I.IF = 0;
/*TODO*///		PUSH(I.sregs[CS]);
/*TODO*///		PUSH(ip);
/*TODO*///		I.sregs[CS] = (WORD)dest_seg;
/*TODO*///		I.base[CS] = SegBase(CS);
/*TODO*///		I.pc = (I.base[CS] + dest_off) & AMASK;
/*TODO*///#ifdef I286
/*TODO*///	}
/*TODO*///#endif
/*TODO*///	CHANGE_PC(I.pc);	
/*TODO*///	
/*TODO*///	I.extra_cycles += cycles.exception;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_trap)(void)
/*TODO*///{
/*TODO*///	PREFIX(_instruction)[FETCHOP]();
/*TODO*///	PREFIX(_interrupt)(1);
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifndef I186
/*TODO*///static void PREFIX86(_rotate_shift_Byte)(unsigned ModRM, unsigned count)
/*TODO*///{
/*TODO*///	unsigned src = (unsigned)GetRMByte(ModRM);
/*TODO*///	unsigned dst=src;
/*TODO*///
/*TODO*///	if (count==0)
/*TODO*///	{
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.rot_reg_base : cycles.rot_m8_base;
/*TODO*///	}
/*TODO*///	else if (count==1)
/*TODO*///	{
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.rot_reg_1 : cycles.rot_m8_1;
/*TODO*///
/*TODO*///		switch (ModRM & 0x38)
/*TODO*///		{
/*TODO*///		case 0x00:	/* ROL eb,1 */
/*TODO*///			I.CarryVal = src & 0x80;
/*TODO*///			dst=(src<<1)+CF;
/*TODO*///			PutbackRMByte(ModRM,dst);
/*TODO*///			I.OverVal = (src^dst)&0x80;
/*TODO*///			break;
/*TODO*///		case 0x08:	/* ROR eb,1 */
/*TODO*///			I.CarryVal = src & 0x01;
/*TODO*///			dst = ((CF<<8)+src) >> 1;
/*TODO*///			PutbackRMByte(ModRM,dst);
/*TODO*///			I.OverVal = (src^dst)&0x80;
/*TODO*///			break;
/*TODO*///		case 0x10:	/* RCL eb,1 */
/*TODO*///			dst=(src<<1)+CF;
/*TODO*///			PutbackRMByte(ModRM,dst);
/*TODO*///			SetCFB(dst);
/*TODO*///			I.OverVal = (src^dst)&0x80;
/*TODO*///			break;
/*TODO*///		case 0x18:	/* RCR eb,1 */
/*TODO*///			dst = ((CF<<8)+src) >> 1;
/*TODO*///			PutbackRMByte(ModRM,dst);
/*TODO*///			I.CarryVal = src & 0x01;
/*TODO*///			I.OverVal = (src^dst)&0x80;
/*TODO*///			break;
/*TODO*///		case 0x20:	/* SHL eb,1 */
/*TODO*///		case 0x30:
/*TODO*///			dst = src << 1;
/*TODO*///			PutbackRMByte(ModRM,dst);
/*TODO*///			SetCFB(dst);
/*TODO*///			I.OverVal = (src^dst)&0x80;
/*TODO*///			I.AuxVal = 1;
/*TODO*///			SetSZPF_Byte(dst);
/*TODO*///			break;
/*TODO*///		case 0x28:	/* SHR eb,1 */
/*TODO*///			dst = src >> 1;
/*TODO*///			PutbackRMByte(ModRM,dst);
/*TODO*///			I.CarryVal = src & 0x01;
/*TODO*///			I.OverVal = src & 0x80;
/*TODO*///			I.AuxVal = 1;
/*TODO*///			SetSZPF_Byte(dst);
/*TODO*///			break;
/*TODO*///		case 0x38:	/* SAR eb,1 */
/*TODO*///			dst = ((INT8)src) >> 1;
/*TODO*///			PutbackRMByte(ModRM,dst);
/*TODO*///			I.CarryVal = src & 0x01;
/*TODO*///			I.OverVal = 0;
/*TODO*///			I.AuxVal = 1;
/*TODO*///			SetSZPF_Byte(dst);
/*TODO*///			break;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.rot_reg_base + cycles.rot_reg_bit : cycles.rot_m8_base + cycles.rot_m8_bit;
/*TODO*///
/*TODO*///		switch (ModRM & 0x38)
/*TODO*///		{
/*TODO*///		case 0x00:	/* ROL eb,count */
/*TODO*///			for (; count > 0; count--)
/*TODO*///			{
/*TODO*///				I.CarryVal = dst & 0x80;
/*TODO*///				dst = (dst << 1) + CF;
/*TODO*///			}
/*TODO*///			PutbackRMByte(ModRM,(BYTE)dst);
/*TODO*///			break;
/*TODO*///		case 0x08:	/* ROR eb,count */
/*TODO*///			for (; count > 0; count--)
/*TODO*///			{
/*TODO*///				I.CarryVal = dst & 0x01;
/*TODO*///				dst = (dst >> 1) + (CF << 7);
/*TODO*///			}
/*TODO*///			PutbackRMByte(ModRM,(BYTE)dst);
/*TODO*///			break;
/*TODO*///		case 0x10:	/* RCL eb,count */
/*TODO*///			for (; count > 0; count--)
/*TODO*///			{
/*TODO*///				dst = (dst << 1) + CF;
/*TODO*///				SetCFB(dst);
/*TODO*///			}
/*TODO*///			PutbackRMByte(ModRM,(BYTE)dst);
/*TODO*///			break;
/*TODO*///		case 0x18:	/* RCR eb,count */
/*TODO*///			for (; count > 0; count--)
/*TODO*///			{
/*TODO*///				dst = (CF<<8)+dst;
/*TODO*///				I.CarryVal = dst & 0x01;
/*TODO*///				dst >>= 1;
/*TODO*///			}
/*TODO*///			PutbackRMByte(ModRM,(BYTE)dst);
/*TODO*///			break;
/*TODO*///		case 0x20:
/*TODO*///		case 0x30:	/* SHL eb,count */
/*TODO*///			dst <<= count;
/*TODO*///			SetCFB(dst);
/*TODO*///			I.AuxVal = 1;
/*TODO*///			SetSZPF_Byte(dst);
/*TODO*///			PutbackRMByte(ModRM,(BYTE)dst);
/*TODO*///			break;
/*TODO*///		case 0x28:	/* SHR eb,count */
/*TODO*///			dst >>= count-1;
/*TODO*///			I.CarryVal = dst & 0x1;
/*TODO*///			dst >>= 1;
/*TODO*///			SetSZPF_Byte(dst);
/*TODO*///			I.AuxVal = 1;
/*TODO*///			PutbackRMByte(ModRM,(BYTE)dst);
/*TODO*///			break;
/*TODO*///		case 0x38:	/* SAR eb,count */
/*TODO*///			dst = ((INT8)dst) >> (count-1);
/*TODO*///			I.CarryVal = dst & 0x1;
/*TODO*///			dst = ((INT8)((BYTE)dst)) >> 1;
/*TODO*///			SetSZPF_Byte(dst);
/*TODO*///			I.AuxVal = 1;
/*TODO*///			PutbackRMByte(ModRM,(BYTE)dst);
/*TODO*///			break;
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_rotate_shift_Word)(unsigned ModRM, unsigned count)
/*TODO*///{
/*TODO*///	unsigned src = GetRMWord(ModRM);
/*TODO*///	unsigned dst=src;
/*TODO*///
/*TODO*///	if (count==0)
/*TODO*///	{
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.rot_reg_base : cycles.rot_m16_base;
/*TODO*///	}
/*TODO*///	else if (count==1)
/*TODO*///	{
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.rot_reg_1 : cycles.rot_m16_1;
/*TODO*///
/*TODO*///		switch (ModRM & 0x38)
/*TODO*///		{
/*TODO*///#if 0
/*TODO*///		case 0x00:	/* ROL ew,1 */
/*TODO*///			tmp2 = (tmp << 1) + CF;
/*TODO*///			SetCFW(tmp2);
/*TODO*///			I.OverVal = !(!(tmp & 0x4000)) != CF;
/*TODO*///			PutbackRMWord(ModRM,tmp2);
/*TODO*///			break;
/*TODO*///		case 0x08:	/* ROR ew,1 */
/*TODO*///			I.CarryVal = tmp & 0x01;
/*TODO*///			tmp2 = (tmp >> 1) + ((unsigned)CF << 15);
/*TODO*///			I.OverVal = !(!(tmp & 0x8000)) != CF;
/*TODO*///			PutbackRMWord(ModRM,tmp2);
/*TODO*///			break;
/*TODO*///		case 0x10:	/* RCL ew,1 */
/*TODO*///			tmp2 = (tmp << 1) + CF;
/*TODO*///			SetCFW(tmp2);
/*TODO*///			I.OverVal = (tmp ^ (tmp << 1)) & 0x8000;
/*TODO*///			PutbackRMWord(ModRM,tmp2);
/*TODO*///			break;
/*TODO*///		case 0x18:	/* RCR ew,1 */
/*TODO*///			tmp2 = (tmp >> 1) + ((unsigned)CF << 15);
/*TODO*///			I.OverVal = !(!(tmp & 0x8000)) != CF;
/*TODO*///			I.CarryVal = tmp & 0x01;
/*TODO*///			PutbackRMWord(ModRM,tmp2);
/*TODO*///			break;
/*TODO*///		case 0x20:	/* SHL ew,1 */
/*TODO*///		case 0x30:
/*TODO*///			tmp <<= 1;
/*TODO*///
/*TODO*///            SetCFW(tmp);
/*TODO*///			SetOFW_Add(tmp,tmp2,tmp2);
/*TODO*///			I.AuxVal = 1;
/*TODO*///			SetSZPF_Word(tmp);
/*TODO*///
/*TODO*///			PutbackRMWord(ModRM,tmp);
/*TODO*///			break;
/*TODO*///		case 0x28:	/* SHR ew,1 */
/*TODO*///			I.CarryVal = tmp & 0x01;
/*TODO*///			I.OverVal = tmp & 0x8000;
/*TODO*///
/*TODO*///			tmp2 = tmp >> 1;
/*TODO*///
/*TODO*///			SetSZPF_Word(tmp2);
/*TODO*///			I.AuxVal = 1;
/*TODO*///			PutbackRMWord(ModRM,tmp2);
/*TODO*///			break;
/*TODO*///			case 0x38:	/* SAR ew,1 */
/*TODO*///			I.CarryVal = tmp & 0x01;
/*TODO*///			I.OverVal = 0;
/*TODO*///
/*TODO*///			tmp2 = (tmp >> 1) | (tmp & 0x8000);
/*TODO*///
/*TODO*///			SetSZPF_Word(tmp2);
/*TODO*///			I.AuxVal = 1;
/*TODO*///			PutbackRMWord(ModRM,tmp2);
/*TODO*///			break;
/*TODO*///#else
/*TODO*///		case 0x00:	/* ROL ew,1 */
/*TODO*///			I.CarryVal = src & 0x8000;
/*TODO*///			dst=(src<<1)+CF;
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			I.OverVal = (src^dst)&0x8000;
/*TODO*///			break;
/*TODO*///		case 0x08:	/* ROR ew,1 */
/*TODO*///			I.CarryVal = src & 0x01;
/*TODO*///			dst = ((CF<<16)+src) >> 1;
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			I.OverVal = (src^dst)&0x8000;
/*TODO*///			break;
/*TODO*///		case 0x10:	/* RCL ew,1 */
/*TODO*///			dst=(src<<1)+CF;
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			SetCFW(dst);
/*TODO*///			I.OverVal = (src^dst)&0x8000;
/*TODO*///			break;
/*TODO*///		case 0x18:	/* RCR ew,1 */
/*TODO*///			dst = ((CF<<16)+src) >> 1;
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			I.CarryVal = src & 0x01;
/*TODO*///			I.OverVal = (src^dst)&0x8000;
/*TODO*///			break;
/*TODO*///		case 0x20:	/* SHL ew,1 */
/*TODO*///		case 0x30:
/*TODO*///			dst = src << 1;
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			SetCFW(dst);
/*TODO*///			I.OverVal = (src^dst)&0x8000;
/*TODO*///			I.AuxVal = 1;
/*TODO*///			SetSZPF_Word(dst);
/*TODO*///			break;
/*TODO*///		case 0x28:	/* SHR ew,1 */
/*TODO*///			dst = src >> 1;
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			I.CarryVal = src & 0x01;
/*TODO*///			I.OverVal = src & 0x8000;
/*TODO*///			I.AuxVal = 1;
/*TODO*///			SetSZPF_Word(dst);
/*TODO*///			break;
/*TODO*///		case 0x38:	/* SAR ew,1 */
/*TODO*///			dst = ((INT16)src) >> 1;
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			I.CarryVal = src & 0x01;
/*TODO*///			I.OverVal = 0;
/*TODO*///			I.AuxVal = 1;
/*TODO*///			SetSZPF_Word(dst);
/*TODO*///			break;
/*TODO*///#endif
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.rot_reg_base + cycles.rot_reg_bit : cycles.rot_m8_base + cycles.rot_m16_bit;
/*TODO*///
/*TODO*///		switch (ModRM & 0x38)
/*TODO*///		{
/*TODO*///		case 0x00:	/* ROL ew,count */
/*TODO*///			for (; count > 0; count--)
/*TODO*///			{
/*TODO*///				I.CarryVal = dst & 0x8000;
/*TODO*///				dst = (dst << 1) + CF;
/*TODO*///			}
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			break;
/*TODO*///		case 0x08:	/* ROR ew,count */
/*TODO*///			for (; count > 0; count--)
/*TODO*///			{
/*TODO*///				I.CarryVal = dst & 0x01;
/*TODO*///				dst = (dst >> 1) + (CF << 15);
/*TODO*///			}
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			break;
/*TODO*///		case 0x10:  /* RCL ew,count */
/*TODO*///			for (; count > 0; count--)
/*TODO*///			{
/*TODO*///				dst = (dst << 1) + CF;
/*TODO*///				SetCFW(dst);
/*TODO*///			}
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			break;
/*TODO*///		case 0x18:	/* RCR ew,count */
/*TODO*///			for (; count > 0; count--)
/*TODO*///			{
/*TODO*///				dst = dst + (CF << 16);
/*TODO*///				I.CarryVal = dst & 0x01;
/*TODO*///				dst >>= 1;
/*TODO*///			}
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			break;
/*TODO*///		case 0x20:
/*TODO*///		case 0x30:	/* SHL ew,count */
/*TODO*///			dst <<= count;
/*TODO*///			SetCFW(dst);
/*TODO*///			I.AuxVal = 1;
/*TODO*///			SetSZPF_Word(dst);
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			break;
/*TODO*///		case 0x28:	/* SHR ew,count */
/*TODO*///			dst >>= count-1;
/*TODO*///			I.CarryVal = dst & 0x1;
/*TODO*///			dst >>= 1;
/*TODO*///			SetSZPF_Word(dst);
/*TODO*///			I.AuxVal = 1;
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			break;
/*TODO*///		case 0x38:	/* SAR ew,count */
/*TODO*///			dst = ((INT16)dst) >> (count-1);
/*TODO*///			I.CarryVal = dst & 0x01;
/*TODO*///			dst = ((INT16)((WORD)dst)) >> 1;
/*TODO*///			SetSZPF_Word(dst);
/*TODO*///			I.AuxVal = 1;
/*TODO*///			PutbackRMWord(ModRM,dst);
/*TODO*///			break;
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///static void PREFIX(rep)(int flagval) 
/*TODO*///{ 
/*TODO*///    /* Handles rep- and repnz- prefixes. flagval is the value of ZF for the 
/*TODO*///		 loop  to continue for CMPS and SCAS instructions. */ 
/*TODO*/// 
/*TODO*///	unsigned next = FETCHOP; 
/*TODO*///	unsigned count = I.regs.w[CX]; 
/*TODO*/// 
/*TODO*///    switch(next) 
/*TODO*///    { 
/*TODO*///	case 0x26:  /* ES: */ 
/*TODO*///		seg_prefix=TRUE; 
/*TODO*///		prefix_base=I.base[ES]; 
/*TODO*///		i86_ICount[0] -= cycles.override;
/*TODO*///		PREFIX(rep)(flagval); 
/*TODO*///		break; 
/*TODO*///	case 0x2e:  /* CS: */ 
/*TODO*///		seg_prefix=TRUE; 
/*TODO*///		prefix_base=I.base[CS]; 
/*TODO*///		i86_ICount[0] -= cycles.override;
/*TODO*///		PREFIX(rep)(flagval); 
/*TODO*///		break; 
/*TODO*///	case 0x36:  /* SS: */ 
/*TODO*///		seg_prefix=TRUE; 
/*TODO*///		prefix_base=I.base[SS]; 
/*TODO*///		i86_ICount[0] -= cycles.override;
/*TODO*///		PREFIX(rep)(flagval); 
/*TODO*///		break; 
/*TODO*///	case 0x3e:  /* DS: */ 
/*TODO*///		seg_prefix=TRUE; 
/*TODO*///		prefix_base=I.base[DS]; 
/*TODO*///		i86_ICount[0] -= cycles.override;
/*TODO*///		PREFIX(rep)(flagval); 
/*TODO*///		break; 
/*TODO*///#ifndef I86 
/*TODO*///	case 0x6c:	/* REP INSB */
/*TODO*///		i86_ICount[0] -= cycles.rep_ins8_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			PutMemB(ES,I.regs.w[DI],read_port(I.regs.w[DX]));
/*TODO*///			I.regs.w[DI] += I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_ins8_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0x6d:  /* REP INSW */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_ins16_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			PutMemB(ES,I.regs.w[DI],read_port(I.regs.w[DX]));
/*TODO*///			PutMemB(ES,I.regs.w[DI]+1,read_port(I.regs.w[DX]+1));
/*TODO*///			I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_ins16_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0x6e:  /* REP OUTSB */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_outs8_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			write_port(I.regs.w[DX],GetMemB(DS,I.regs.w[SI]));
/*TODO*///			I.regs.w[DI] += I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_outs8_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0x6f:  /* REP OUTSW */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_outs16_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			write_port(I.regs.w[DX],GetMemB(DS,I.regs.w[SI]));
/*TODO*///			write_port(I.regs.w[DX]+1,GetMemB(DS,I.regs.w[SI]+1));
/*TODO*///			I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_outs16_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///#endif 
/*TODO*///	case 0xa4:  /* REP MOVSB */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_movs8_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			BYTE tmp;
/*TODO*///			
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			tmp = GetMemB(DS,I.regs.w[SI]);
/*TODO*///			PutMemB(ES,I.regs.w[DI], tmp);
/*TODO*///			I.regs.w[DI] += I.DirVal;
/*TODO*///			I.regs.w[SI] += I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_movs8_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xa5:  /* REP MOVSW */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_movs16_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			WORD tmp;
/*TODO*///			
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			tmp = GetMemW(DS,I.regs.w[SI]);
/*TODO*///			PutMemW(ES,I.regs.w[DI], tmp);
/*TODO*///			I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///			I.regs.w[SI] += 2 * I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_movs16_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xa6:  /* REP(N)E CMPSB */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_cmps8_base;
/*TODO*///		for (I.ZeroVal = !flagval; (ZF == flagval) && (count > 0); count--) 
/*TODO*///		{
/*TODO*///			unsigned dst, src;
/*TODO*///			
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			dst = GetMemB(ES, I.regs.w[DI]);
/*TODO*///			src = GetMemB(DS, I.regs.w[SI]);
/*TODO*///		    SUBB(src,dst); /* opposite of the usual convention */
/*TODO*///			I.regs.w[DI] += I.DirVal;
/*TODO*///			I.regs.w[SI] += I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_cmps8_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xa7:  /* REP(N)E CMPSW */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_cmps16_base;
/*TODO*///		for (I.ZeroVal = !flagval; (ZF == flagval) && (count > 0); count--) 
/*TODO*///		{
/*TODO*///			unsigned dst, src;
/*TODO*///			
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			dst = GetMemB(ES, I.regs.w[DI]);
/*TODO*///			src = GetMemB(DS, I.regs.w[SI]);
/*TODO*///		    SUBB(src,dst); /* opposite of the usual convention */
/*TODO*///			I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///			I.regs.w[SI] += 2 * I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_cmps16_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xaa:  /* REP STOSB */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_stos8_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			PutMemB(ES,I.regs.w[DI],I.regs.b[AL]);
/*TODO*///			I.regs.w[DI] += I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_stos8_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xab:  /* REP STOSW */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_stos16_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			PutMemB(ES,I.regs.w[DI],I.regs.b[AL]);
/*TODO*///			PutMemB(ES,I.regs.w[DI]+1,I.regs.b[AH]);
/*TODO*///			I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_stos16_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xac:  /* REP LODSB */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_lods8_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			I.regs.b[AL] = GetMemB(DS,I.regs.w[SI]);
/*TODO*///			I.regs.w[SI] += I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_lods8_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xad:  /* REP LODSW */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_lods16_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			I.regs.w[AX] = GetMemW(DS,I.regs.w[SI]);
/*TODO*///			I.regs.w[SI] += 2 * I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_lods16_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xae:  /* REP(N)E SCASB */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_scas8_base;
/*TODO*///		for (I.ZeroVal = !flagval; (ZF == flagval) && (count > 0); count--) 
/*TODO*///		{
/*TODO*///			unsigned src, dst;
/*TODO*///			
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			src = GetMemB(ES, I.regs.w[DI]);
/*TODO*///			dst = I.regs.b[AL];
/*TODO*///		    SUBB(dst,src);
/*TODO*///			I.regs.w[DI] += I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_scas8_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xaf:  /* REP(N)E SCASW */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_scas16_base;
/*TODO*///		for (I.ZeroVal = !flagval; (ZF == flagval) && (count > 0); count--) 
/*TODO*///		{
/*TODO*///			unsigned src, dst;
/*TODO*///			
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			src = GetMemW(ES, I.regs.w[DI]);
/*TODO*///			dst = I.regs.w[AX];
/*TODO*///		    SUBW(dst,src);
/*TODO*///			I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_scas16_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	default: 
/*TODO*///		PREFIX(_instruction)[next](); 
/*TODO*///	} 
/*TODO*///} 
/*TODO*///
/*TODO*///#ifndef I186
/*TODO*///static void PREFIX86(_add_br8)(void)    /* Opcode 0x00 */
/*TODO*///{
/*TODO*///	DEF_br8(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_mr8;
/*TODO*///	ADDB(dst,src);
/*TODO*///    PutbackRMByte(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_add_wr16)(void)    /* Opcode 0x01 */
/*TODO*///{
/*TODO*///    DEF_wr16(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_mr16;
/*TODO*///	ADDW(dst,src);
/*TODO*///	PutbackRMWord(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_add_r8b)(void)    /* Opcode 0x02 */
/*TODO*///{
/*TODO*///	DEF_r8b(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_rm8;
/*TODO*///    ADDB(dst,src);
/*TODO*///    RegByte(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_add_r16w)(void)    /* Opcode 0x03 */
/*TODO*///{
/*TODO*///    DEF_r16w(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_rm16;
/*TODO*///	ADDW(dst,src);
/*TODO*///	RegWord(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_add_ald8)(void)    /* Opcode 0x04 */
/*TODO*///{
/*TODO*///    DEF_ald8(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri8;
/*TODO*///    ADDB(dst,src);
/*TODO*///	I.regs.b[AL]=dst;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_add_axd16)(void)    /* Opcode 0x05 */
/*TODO*///{
/*TODO*///    DEF_axd16(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri16;
/*TODO*///	ADDW(dst,src);
/*TODO*///	I.regs.w[AX]=dst;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_push_es)(void)    /* Opcode 0x06 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.push_seg;
/*TODO*///	PUSH(I.sregs[ES]);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_pop_es)(void)    /* Opcode 0x07 */
/*TODO*///{
/*TODO*///	POP(I.sregs[ES]);
/*TODO*///	I.base[ES] = SegBase(ES);
/*TODO*///	i86_ICount[0] -= cycles.pop_seg;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_or_br8)(void)    /* Opcode 0x08 */
/*TODO*///{
/*TODO*///    DEF_br8(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_mr8;
/*TODO*///	ORB(dst,src);
/*TODO*///    PutbackRMByte(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_or_wr16)(void)    /* Opcode 0x09 */
/*TODO*///{
/*TODO*///	DEF_wr16(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_mr16;
/*TODO*///    ORW(dst,src);
/*TODO*///    PutbackRMWord(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_or_r8b)(void)    /* Opcode 0x0a */
/*TODO*///{
/*TODO*///	DEF_r8b(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_rm8;
/*TODO*///    ORB(dst,src);
/*TODO*///    RegByte(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_or_r16w)(void)    /* Opcode 0x0b */
/*TODO*///{
/*TODO*///    DEF_r16w(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_rm16;
/*TODO*///    ORW(dst,src);
/*TODO*///    RegWord(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_or_ald8)(void)    /* Opcode 0x0c */
/*TODO*///{
/*TODO*///    DEF_ald8(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri8;
/*TODO*///    ORB(dst,src);
/*TODO*///	I.regs.b[AL]=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_or_axd16)(void)    /* Opcode 0x0d */
/*TODO*///{
/*TODO*///    DEF_axd16(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri16;
/*TODO*///	ORW(dst,src);
/*TODO*///	I.regs.w[AX]=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_push_cs)(void)    /* Opcode 0x0e */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.push_seg;
/*TODO*///	PUSH(I.sregs[CS]);
/*TODO*///}
/*TODO*///
/*TODO*////* Opcode 0x0f invalid */
/*TODO*///
/*TODO*///static void PREFIX86(_adc_br8)(void)    /* Opcode 0x10 */
/*TODO*///{
/*TODO*///    DEF_br8(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_mr8;
/*TODO*///    src+=CF;
/*TODO*///    ADDB(dst,src);
/*TODO*///    PutbackRMByte(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_adc_wr16)(void)    /* Opcode 0x11 */
/*TODO*///{
/*TODO*///    DEF_wr16(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_mr16;
/*TODO*///    src+=CF;
/*TODO*///    ADDW(dst,src);
/*TODO*///	PutbackRMWord(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_adc_r8b)(void)    /* Opcode 0x12 */
/*TODO*///{
/*TODO*///    DEF_r8b(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_rm8;
/*TODO*///	src+=CF;
/*TODO*///    ADDB(dst,src);
/*TODO*///    RegByte(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_adc_r16w)(void)    /* Opcode 0x13 */
/*TODO*///{
/*TODO*///    DEF_r16w(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_rm16;
/*TODO*///	src+=CF;
/*TODO*///    ADDW(dst,src);
/*TODO*///    RegWord(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_adc_ald8)(void)    /* Opcode 0x14 */
/*TODO*///{
/*TODO*///    DEF_ald8(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri8;
/*TODO*///    src+=CF;
/*TODO*///    ADDB(dst,src);
/*TODO*///	I.regs.b[AL] = dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_adc_axd16)(void)    /* Opcode 0x15 */
/*TODO*///{
/*TODO*///	DEF_axd16(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri16;
/*TODO*///	src+=CF;
/*TODO*///    ADDW(dst,src);
/*TODO*///	I.regs.w[AX]=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_push_ss)(void)    /* Opcode 0x16 */
/*TODO*///{
/*TODO*///	PUSH(I.sregs[SS]);
/*TODO*///	i86_ICount[0] -= cycles.push_seg;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_pop_ss)(void)    /* Opcode 0x17 */
/*TODO*///{
/*TODO*///#ifdef I286
/*TODO*///	UINT16 tmp;
/*TODO*///	POP(tmp);
/*TODO*///	i286_data_descriptor(SS, tmp);
/*TODO*///#else
/*TODO*///	POP(I.sregs[SS]);
/*TODO*///	I.base[SS] = SegBase(SS);
/*TODO*///#endif
/*TODO*///	i86_ICount[0] -= cycles.pop_seg;
/*TODO*///	PREFIX(_instruction)[FETCHOP](); /* no interrupt before next instruction */
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sbb_br8)(void)    /* Opcode 0x18 */
/*TODO*///{
/*TODO*///    DEF_br8(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_mr8;
/*TODO*///    src+=CF;
/*TODO*///    SUBB(dst,src);
/*TODO*///    PutbackRMByte(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sbb_wr16)(void)    /* Opcode 0x19 */
/*TODO*///{
/*TODO*///    DEF_wr16(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_mr16;
/*TODO*///    src+=CF;
/*TODO*///	SUBW(dst,src);
/*TODO*///	PutbackRMWord(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sbb_r8b)(void)    /* Opcode 0x1a */
/*TODO*///{
/*TODO*///	DEF_r8b(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_rm8;
/*TODO*///	src+=CF;
/*TODO*///    SUBB(dst,src);
/*TODO*///    RegByte(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sbb_r16w)(void)    /* Opcode 0x1b */
/*TODO*///{
/*TODO*///    DEF_r16w(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_rm16;
/*TODO*///	src+=CF;
/*TODO*///    SUBW(dst,src);
/*TODO*///    RegWord(ModRM)= dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sbb_ald8)(void)    /* Opcode 0x1c */
/*TODO*///{
/*TODO*///    DEF_ald8(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri8;
/*TODO*///    src+=CF;
/*TODO*///    SUBB(dst,src);
/*TODO*///	I.regs.b[AL] = dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sbb_axd16)(void)    /* Opcode 0x1d */
/*TODO*///{
/*TODO*///    DEF_axd16(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri16;
/*TODO*///	src+=CF;
/*TODO*///    SUBW(dst,src);
/*TODO*///	I.regs.w[AX]=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_push_ds)(void)    /* Opcode 0x1e */
/*TODO*///{
/*TODO*///	PUSH(I.sregs[DS]);
/*TODO*///	i86_ICount[0] -= cycles.push_seg;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_pop_ds)(void)    /* Opcode 0x1f */
/*TODO*///{
/*TODO*///#ifdef I286
/*TODO*///	UINT16 tmp;
/*TODO*///	POP(tmp);
/*TODO*///	i286_data_descriptor(DS,tmp);
/*TODO*///#else
/*TODO*///	POP(I.sregs[DS]);
/*TODO*///	I.base[DS] = SegBase(DS);
/*TODO*///#endif
/*TODO*///	i86_ICount[0] -= cycles.push_seg;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_and_br8)(void)    /* Opcode 0x20 */
/*TODO*///{
/*TODO*///    DEF_br8(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_mr8;
/*TODO*///    ANDB(dst,src);
/*TODO*///    PutbackRMByte(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_and_wr16)(void)    /* Opcode 0x21 */
/*TODO*///{
/*TODO*///    DEF_wr16(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_mr16;
/*TODO*///    ANDW(dst,src);
/*TODO*///    PutbackRMWord(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_and_r8b)(void)    /* Opcode 0x22 */
/*TODO*///{
/*TODO*///    DEF_r8b(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_rm8;
/*TODO*///    ANDB(dst,src);
/*TODO*///    RegByte(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_and_r16w)(void)    /* Opcode 0x23 */
/*TODO*///{
/*TODO*///    DEF_r16w(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_rm16;
/*TODO*///	ANDW(dst,src);
/*TODO*///    RegWord(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_and_ald8)(void)    /* Opcode 0x24 */
/*TODO*///{
/*TODO*///    DEF_ald8(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri8;
/*TODO*///    ANDB(dst,src);
/*TODO*///	I.regs.b[AL] = dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_and_axd16)(void)    /* Opcode 0x25 */
/*TODO*///{
/*TODO*///    DEF_axd16(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri16;
/*TODO*///    ANDW(dst,src);
/*TODO*///	I.regs.w[AX]=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_es)(void)    /* Opcode 0x26 */
/*TODO*///{
/*TODO*///	seg_prefix=TRUE;
/*TODO*///	prefix_base=I.base[ES];
/*TODO*///	i86_ICount[0] -= cycles.override;
/*TODO*///	PREFIX(_instruction)[FETCHOP]();
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_daa)(void)    /* Opcode 0x27 */
/*TODO*///{
/*TODO*///	if (AF || ((I.regs.b[AL] & 0xf) > 9))
/*TODO*///	{
/*TODO*///		int tmp;
/*TODO*///		I.regs.b[AL] = tmp = I.regs.b[AL] + 6;
/*TODO*///		I.AuxVal = 1;
/*TODO*///		I.CarryVal |= tmp & 0x100;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (CF || (I.regs.b[AL] > 0x9f))
/*TODO*///	{
/*TODO*///		I.regs.b[AL] += 0x60;
/*TODO*///		I.CarryVal = 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	SetSZPF_Byte(I.regs.b[AL]);
/*TODO*///	i86_ICount[0] -= cycles.daa;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sub_br8)(void)    /* Opcode 0x28 */
/*TODO*///{
/*TODO*///    DEF_br8(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_mr8;
/*TODO*///    SUBB(dst,src);
/*TODO*///    PutbackRMByte(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sub_wr16)(void)    /* Opcode 0x29 */
/*TODO*///{
/*TODO*///    DEF_wr16(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_mr16;
/*TODO*///    SUBW(dst,src);
/*TODO*///    PutbackRMWord(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sub_r8b)(void)    /* Opcode 0x2a */
/*TODO*///{
/*TODO*///    DEF_r8b(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_rm8;
/*TODO*///	SUBB(dst,src);
/*TODO*///    RegByte(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sub_r16w)(void)    /* Opcode 0x2b */
/*TODO*///{
/*TODO*///    DEF_r16w(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_rm16;
/*TODO*///    SUBW(dst,src);
/*TODO*///    RegWord(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sub_ald8)(void)    /* Opcode 0x2c */
/*TODO*///{
/*TODO*///    DEF_ald8(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri8;
/*TODO*///    SUBB(dst,src);
/*TODO*///	I.regs.b[AL] = dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sub_axd16)(void)    /* Opcode 0x2d */
/*TODO*///{
/*TODO*///	DEF_axd16(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri16;
/*TODO*///    SUBW(dst,src);
/*TODO*///	I.regs.w[AX]=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_cs)(void)    /* Opcode 0x2e */
/*TODO*///{
/*TODO*///    seg_prefix=TRUE;
/*TODO*///	prefix_base=I.base[CS];
/*TODO*///	i86_ICount[0] -= cycles.override;
/*TODO*///	PREFIX(_instruction)[FETCHOP]();
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_das)(void)    /* Opcode 0x2f */
/*TODO*///{
/*TODO*///	if (AF || ((I.regs.b[AL] & 0xf) > 9))
/*TODO*///	{
/*TODO*///		int tmp;
/*TODO*///		I.regs.b[AL] = tmp = I.regs.b[AL] - 6;
/*TODO*///		I.AuxVal = 1;
/*TODO*///		I.CarryVal |= tmp & 0x100;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (CF || (I.regs.b[AL] > 0x9f))
/*TODO*///	{
/*TODO*///		I.regs.b[AL] -= 0x60;
/*TODO*///		I.CarryVal = 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	SetSZPF_Byte(I.regs.b[AL]);
/*TODO*///	i86_ICount[0] -= cycles.das;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xor_br8)(void)    /* Opcode 0x30 */
/*TODO*///{
/*TODO*///    DEF_br8(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_mr8;
/*TODO*///    XORB(dst,src);
/*TODO*///	PutbackRMByte(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xor_wr16)(void)    /* Opcode 0x31 */
/*TODO*///{
/*TODO*///	DEF_wr16(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_mr16;
/*TODO*///	XORW(dst,src);
/*TODO*///    PutbackRMWord(ModRM,dst);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xor_r8b)(void)    /* Opcode 0x32 */
/*TODO*///{
/*TODO*///    DEF_r8b(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_rm8;
/*TODO*///    XORB(dst,src);
/*TODO*///    RegByte(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xor_r16w)(void)    /* Opcode 0x33 */
/*TODO*///{
/*TODO*///    DEF_r16w(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_rm16;
/*TODO*///    XORW(dst,src);
/*TODO*///	RegWord(ModRM)=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xor_ald8)(void)    /* Opcode 0x34 */
/*TODO*///{
/*TODO*///	DEF_ald8(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri8;
/*TODO*///    XORB(dst,src);
/*TODO*///	I.regs.b[AL] = dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xor_axd16)(void)    /* Opcode 0x35 */
/*TODO*///{
/*TODO*///    DEF_axd16(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri16;
/*TODO*///    XORW(dst,src);
/*TODO*///	I.regs.w[AX]=dst;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_ss)(void)    /* Opcode 0x36 */
/*TODO*///{
/*TODO*///	seg_prefix=TRUE;
/*TODO*///	prefix_base=I.base[SS];
/*TODO*///	i86_ICount[0] -= cycles.override;
/*TODO*///	PREFIX(_instruction)[FETCHOP]();
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_aaa)(void)    /* Opcode 0x37 */
/*TODO*///{
/*TODO*///	if (AF || ((I.regs.b[AL] & 0xf) > 9))
/*TODO*///    {
/*TODO*///		I.regs.b[AL] += 6;
/*TODO*///		I.regs.b[AH] += 1;
/*TODO*///		I.AuxVal = 1;
/*TODO*///		I.CarryVal = 1;
/*TODO*///    }
/*TODO*///	else
/*TODO*///	{
/*TODO*///		I.AuxVal = 0;
/*TODO*///		I.CarryVal = 0;
/*TODO*///    }
/*TODO*///	I.regs.b[AL] &= 0x0F;
/*TODO*///	i86_ICount[0] -= cycles.aaa;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_cmp_br8)(void)    /* Opcode 0x38 */
/*TODO*///{
/*TODO*///    DEF_br8(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_rm8;
/*TODO*///    SUBB(dst,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_cmp_wr16)(void)    /* Opcode 0x39 */
/*TODO*///{
/*TODO*///	DEF_wr16(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_rm16;
/*TODO*///    SUBW(dst,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_cmp_r8b)(void)    /* Opcode 0x3a */
/*TODO*///{
/*TODO*///    DEF_r8b(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_rm8;
/*TODO*///    SUBB(dst,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_cmp_r16w)(void)    /* Opcode 0x3b */
/*TODO*///{
/*TODO*///	DEF_r16w(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_rm16;
/*TODO*///    SUBW(dst,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_cmp_ald8)(void)    /* Opcode 0x3c */
/*TODO*///{
/*TODO*///    DEF_ald8(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri8;
/*TODO*///    SUBB(dst,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_cmp_axd16)(void)    /* Opcode 0x3d */
/*TODO*///{
/*TODO*///    DEF_axd16(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri16;
/*TODO*///    SUBW(dst,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_ds)(void)    /* Opcode 0x3e */
/*TODO*///{
/*TODO*///	seg_prefix=TRUE;
/*TODO*///	prefix_base=I.base[DS];
/*TODO*///	i86_ICount[0] -= cycles.override;
/*TODO*///	PREFIX(_instruction)[FETCHOP]();
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_aas)(void)    /* Opcode 0x3f */
/*TODO*///{
/*TODO*///	if (AF || ((I.regs.b[AL] & 0xf) > 9))
/*TODO*///    {
/*TODO*///		I.regs.b[AL] -= 6;
/*TODO*///		I.regs.b[AH] -= 1;
/*TODO*///		I.AuxVal = 1;
/*TODO*///		I.CarryVal = 1;
/*TODO*///    }
/*TODO*///	else
/*TODO*///	{
/*TODO*///		I.AuxVal = 0;
/*TODO*///		I.CarryVal = 0;
/*TODO*///    }
/*TODO*///	I.regs.b[AL] &= 0x0F;
/*TODO*///	i86_ICount[0] -= cycles.aas;
/*TODO*///}
/*TODO*///
/*TODO*///#define IncWordReg(Reg) 					\
/*TODO*///{											\
/*TODO*///	unsigned tmp = (unsigned)I.regs.w[Reg]; \
/*TODO*///	unsigned tmp1 = tmp+1;					\
/*TODO*///	SetOFW_Add(tmp1,tmp,1); 				\
/*TODO*///	SetAF(tmp1,tmp,1);						\
/*TODO*///	SetSZPF_Word(tmp1); 					\
/*TODO*///	I.regs.w[Reg]=tmp1; 					\
/*TODO*///	i86_ICount[0] -= cycles.incdec_r16;			\
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_inc_ax)(void)    /* Opcode 0x40 */
/*TODO*///{
/*TODO*///    IncWordReg(AX);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_inc_cx)(void)    /* Opcode 0x41 */
/*TODO*///{
/*TODO*///    IncWordReg(CX);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_inc_dx)(void)    /* Opcode 0x42 */
/*TODO*///{
/*TODO*///	IncWordReg(DX);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX(_inc_bx)(void)    /* Opcode 0x43 */
/*TODO*///{
/*TODO*///	IncWordReg(BX);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_inc_sp)(void)    /* Opcode 0x44 */
/*TODO*///{
/*TODO*///    IncWordReg(SP);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_inc_bp)(void)    /* Opcode 0x45 */
/*TODO*///{
/*TODO*///	IncWordReg(BP);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_inc_si)(void)    /* Opcode 0x46 */
/*TODO*///{
/*TODO*///	IncWordReg(SI);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_inc_di)(void)    /* Opcode 0x47 */
/*TODO*///{
/*TODO*///    IncWordReg(DI);
/*TODO*///}
/*TODO*///
/*TODO*///#define DecWordReg(Reg) 					\
/*TODO*///{ 											\
/*TODO*///	unsigned tmp = (unsigned)I.regs.w[Reg]; \
/*TODO*///    unsigned tmp1 = tmp-1; 					\
/*TODO*///    SetOFW_Sub(tmp1,1,tmp); 				\
/*TODO*///    SetAF(tmp1,tmp,1); 						\
/*TODO*///	SetSZPF_Word(tmp1);						\
/*TODO*///	I.regs.w[Reg]=tmp1; 					\
/*TODO*///	i86_ICount[0] -= cycles.incdec_r16;			\
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_dec_ax)(void)    /* Opcode 0x48 */
/*TODO*///{
/*TODO*///    DecWordReg(AX);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_dec_cx)(void)    /* Opcode 0x49 */
/*TODO*///{
/*TODO*///	DecWordReg(CX);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_dec_dx)(void)    /* Opcode 0x4a */
/*TODO*///{
/*TODO*///	DecWordReg(DX);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_dec_bx)(void)    /* Opcode 0x4b */
/*TODO*///{
/*TODO*///	DecWordReg(BX);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_dec_sp)(void)    /* Opcode 0x4c */
/*TODO*///{
/*TODO*///    DecWordReg(SP);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_dec_bp)(void)    /* Opcode 0x4d */
/*TODO*///{
/*TODO*///    DecWordReg(BP);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_dec_si)(void)    /* Opcode 0x4e */
/*TODO*///{
/*TODO*///    DecWordReg(SI);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_dec_di)(void)    /* Opcode 0x4f */
/*TODO*///{
/*TODO*///    DecWordReg(DI);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_push_ax)(void)    /* Opcode 0x50 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.push_r16;
/*TODO*///	PUSH(I.regs.w[AX]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_push_cx)(void)    /* Opcode 0x51 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.push_r16;
/*TODO*///	PUSH(I.regs.w[CX]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_push_dx)(void)    /* Opcode 0x52 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.push_r16;
/*TODO*///	PUSH(I.regs.w[DX]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_push_bx)(void)    /* Opcode 0x53 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.push_r16;
/*TODO*///	PUSH(I.regs.w[BX]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_push_sp)(void)    /* Opcode 0x54 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.push_r16;
/*TODO*///	PUSH(I.regs.w[SP]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_push_bp)(void)    /* Opcode 0x55 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.push_r16;
/*TODO*///	PUSH(I.regs.w[BP]);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_push_si)(void)    /* Opcode 0x56 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.push_r16;
/*TODO*///	PUSH(I.regs.w[SI]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_push_di)(void)    /* Opcode 0x57 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.push_r16;
/*TODO*///	PUSH(I.regs.w[DI]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_pop_ax)(void)    /* Opcode 0x58 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.pop_r16;
/*TODO*///	POP(I.regs.w[AX]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_pop_cx)(void)    /* Opcode 0x59 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.pop_r16;
/*TODO*///	POP(I.regs.w[CX]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_pop_dx)(void)    /* Opcode 0x5a */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.pop_r16;
/*TODO*///	POP(I.regs.w[DX]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_pop_bx)(void)    /* Opcode 0x5b */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.pop_r16;
/*TODO*///	POP(I.regs.w[BX]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_pop_sp)(void)    /* Opcode 0x5c */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.pop_r16;
/*TODO*///	POP(I.regs.w[SP]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_pop_bp)(void)    /* Opcode 0x5d */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.pop_r16;
/*TODO*///	POP(I.regs.w[BP]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_pop_si)(void)    /* Opcode 0x5e */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.pop_r16;
/*TODO*///	POP(I.regs.w[SI]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_pop_di)(void)    /* Opcode 0x5f */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.pop_r16;
/*TODO*///	POP(I.regs.w[DI]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jo)(void)    /* Opcode 0x70 */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///	if (OF)
/*TODO*///	{
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jno)(void)    /* Opcode 0x71 */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///	if (!OF) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jb)(void)    /* Opcode 0x72 */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///	if (CF) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jnb)(void)    /* Opcode 0x73 */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///	if (!CF) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jz)(void)    /* Opcode 0x74 */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///	if (ZF) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jnz)(void)    /* Opcode 0x75 */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///	if (!ZF) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jbe)(void)    /* Opcode 0x76 */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///    if (CF || ZF) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jnbe)(void)    /* Opcode 0x77 */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///	 if (!(CF || ZF)) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_js)(void)    /* Opcode 0x78 */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///    if (SF) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jns)(void)    /* Opcode 0x79 */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///    if (!SF) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jp)(void)    /* Opcode 0x7a */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///    if (PF) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jnp)(void)    /* Opcode 0x7b */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///    if (!PF) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jl)(void)    /* Opcode 0x7c */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///    if ((SF!=OF)&&!ZF) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jnl)(void)    /* Opcode 0x7d */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///    if (ZF||(SF==OF)) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jle)(void)    /* Opcode 0x7e */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///    if (ZF||(SF!=OF)) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jnle)(void)    /* Opcode 0x7f */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///    if ((SF==OF)&&!ZF) {
/*TODO*///		I.pc += tmp;
/*TODO*///		i86_ICount[0] -= cycles.jcc_t;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.jcc_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_80pre)(void)    /* Opcode 0x80 */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCHOP;
/*TODO*///	unsigned dst = GetRMByte(ModRM);
/*TODO*///    unsigned src = FETCH;
/*TODO*///
/*TODO*///    switch (ModRM & 0x38)
/*TODO*///	{
/*TODO*///    case 0x00:  /* ADD eb,d8 */
/*TODO*///        ADDB(dst,src);
/*TODO*///        PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///    case 0x08:  /* OR eb,d8 */
/*TODO*///        ORB(dst,src);
/*TODO*///		PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///    case 0x10:  /* ADC eb,d8 */
/*TODO*///        src+=CF;
/*TODO*///        ADDB(dst,src);
/*TODO*///		PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///    case 0x18:  /* SBB eb,b8 */
/*TODO*///		src+=CF;
/*TODO*///		SUBB(dst,src);
/*TODO*///        PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///    case 0x20:  /* AND eb,d8 */
/*TODO*///        ANDB(dst,src);
/*TODO*///		PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///    case 0x28:  /* SUB eb,d8 */
/*TODO*///        SUBB(dst,src);
/*TODO*///        PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///    case 0x30:  /* XOR eb,d8 */
/*TODO*///        XORB(dst,src);
/*TODO*///		PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///    case 0x38:  /* CMP eb,d8 */
/*TODO*///        SUBB(dst,src);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8_ro;
/*TODO*///		break;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_81pre)(void)    /* Opcode 0x81 */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///	unsigned dst = GetRMWord(ModRM);
/*TODO*///    unsigned src = FETCH;
/*TODO*///    src+= (FETCH << 8);
/*TODO*///
/*TODO*///	switch (ModRM & 0x38)
/*TODO*///    {
/*TODO*///    case 0x00:  /* ADD ew,d16 */
/*TODO*///		ADDW(dst,src);
/*TODO*///		PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri16 : cycles.alu_mi16;
/*TODO*///		break;
/*TODO*///    case 0x08:  /* OR ew,d16 */
/*TODO*///        ORW(dst,src);
/*TODO*///        PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri16 : cycles.alu_mi16;
/*TODO*///		break;
/*TODO*///	case 0x10:	/* ADC ew,d16 */
/*TODO*///		src+=CF;
/*TODO*///		ADDW(dst,src);
/*TODO*///        PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri16 : cycles.alu_mi16;
/*TODO*///		break;
/*TODO*///    case 0x18:  /* SBB ew,d16 */
/*TODO*///        src+=CF;
/*TODO*///		SUBW(dst,src);
/*TODO*///        PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri16 : cycles.alu_mi16;
/*TODO*///		break;
/*TODO*///    case 0x20:  /* AND ew,d16 */
/*TODO*///        ANDW(dst,src);
/*TODO*///		PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri16 : cycles.alu_mi16;
/*TODO*///		break;
/*TODO*///    case 0x28:  /* SUB ew,d16 */
/*TODO*///        SUBW(dst,src);
/*TODO*///        PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri16 : cycles.alu_mi16;
/*TODO*///		break;
/*TODO*///    case 0x30:  /* XOR ew,d16 */
/*TODO*///		XORW(dst,src);
/*TODO*///        PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri16 : cycles.alu_mi16;
/*TODO*///		break;
/*TODO*///    case 0x38:  /* CMP ew,d16 */
/*TODO*///        SUBW(dst,src);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri16 : cycles.alu_mi16_ro;
/*TODO*///		break;
/*TODO*///    }
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_82pre)(void)	 /* Opcode 0x82 */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///	unsigned dst = GetRMByte(ModRM);
/*TODO*///	unsigned src = FETCH;
/*TODO*///
/*TODO*///	switch (ModRM & 0x38)
/*TODO*///	{
/*TODO*///	case 0x00:	/* ADD eb,d8 */
/*TODO*///		ADDB(dst,src);
/*TODO*///		PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///	case 0x08:	/* OR eb,d8 */
/*TODO*///		ORB(dst,src);
/*TODO*///		PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///	case 0x10:	/* ADC eb,d8 */
/*TODO*///		src+=CF;
/*TODO*///		ADDB(dst,src);
/*TODO*///		PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///	case 0x18:	/* SBB eb,d8 */
/*TODO*///        src+=CF;
/*TODO*///		SUBB(dst,src);
/*TODO*///		PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///	case 0x20:	/* AND eb,d8 */
/*TODO*///		ANDB(dst,src);
/*TODO*///		PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///	case 0x28:	/* SUB eb,d8 */
/*TODO*///		SUBB(dst,src);
/*TODO*///		PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///	case 0x30:	/* XOR eb,d8 */
/*TODO*///		XORB(dst,src);
/*TODO*///		PutbackRMByte(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8;
/*TODO*///		break;
/*TODO*///	case 0x38:	/* CMP eb,d8 */
/*TODO*///		SUBB(dst,src);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8_ro;
/*TODO*///		break;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_83pre)(void)    /* Opcode 0x83 */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///    unsigned dst = GetRMWord(ModRM);
/*TODO*///    unsigned src = (WORD)((INT16)((INT8)FETCH));
/*TODO*///
/*TODO*///	switch (ModRM & 0x38)
/*TODO*///    {
/*TODO*///    case 0x00:  /* ADD ew,d16 */
/*TODO*///        ADDW(dst,src);
/*TODO*///        PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_r16i8 : cycles.alu_m16i8;
/*TODO*///		break;
/*TODO*///    case 0x08:  /* OR ew,d16 */
/*TODO*///		ORW(dst,src);
/*TODO*///        PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_r16i8 : cycles.alu_m16i8;
/*TODO*///		break;
/*TODO*///    case 0x10:  /* ADC ew,d16 */
/*TODO*///        src+=CF;
/*TODO*///		ADDW(dst,src);
/*TODO*///        PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_r16i8 : cycles.alu_m16i8;
/*TODO*///		break;
/*TODO*///	case 0x18:	/* SBB ew,d16 */
/*TODO*///		src+=CF;
/*TODO*///        SUBW(dst,src);
/*TODO*///        PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_r16i8 : cycles.alu_m16i8;
/*TODO*///		break;
/*TODO*///	case 0x20:	/* AND ew,d16 */
/*TODO*///        ANDW(dst,src);
/*TODO*///        PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_r16i8 : cycles.alu_m16i8;
/*TODO*///		break;
/*TODO*///    case 0x28:  /* SUB ew,d16 */
/*TODO*///		SUBW(dst,src);
/*TODO*///		PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_r16i8 : cycles.alu_m16i8;
/*TODO*///		break;
/*TODO*///    case 0x30:  /* XOR ew,d16 */
/*TODO*///		XORW(dst,src);
/*TODO*///        PutbackRMWord(ModRM,dst);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_r16i8 : cycles.alu_m16i8;
/*TODO*///		break;
/*TODO*///    case 0x38:  /* CMP ew,d16 */
/*TODO*///        SUBW(dst,src);
/*TODO*///        i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_r16i8 : cycles.alu_m16i8_ro;
/*TODO*///		break;
/*TODO*///    }
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_test_br8)(void)    /* Opcode 0x84 */
/*TODO*///{
/*TODO*///    DEF_br8(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr8 : cycles.alu_rm8;
/*TODO*///    ANDB(dst,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_test_wr16)(void)    /* Opcode 0x85 */
/*TODO*///{
/*TODO*///    DEF_wr16(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_rr16 : cycles.alu_rm16;
/*TODO*///	ANDW(dst,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xchg_br8)(void)    /* Opcode 0x86 */
/*TODO*///{
/*TODO*///    DEF_br8(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.xchg_rr8 : cycles.xchg_rm8;
/*TODO*///    RegByte(ModRM)=dst;
/*TODO*///    PutbackRMByte(ModRM,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xchg_wr16)(void)    /* Opcode 0x87 */
/*TODO*///{
/*TODO*///    DEF_wr16(dst,src);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.xchg_rr16 : cycles.xchg_rm16;
/*TODO*///    RegWord(ModRM)=dst;
/*TODO*///    PutbackRMWord(ModRM,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_br8)(void)    /* Opcode 0x88 */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///    BYTE src = RegByte(ModRM);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.mov_rr8 : cycles.mov_mr8;
/*TODO*///    PutRMByte(ModRM,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_wr16)(void)    /* Opcode 0x89 */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///    WORD src = RegWord(ModRM);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.mov_rr16 : cycles.mov_mr16;
/*TODO*///    PutRMWord(ModRM,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_r8b)(void)    /* Opcode 0x8a */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///    BYTE src = GetRMByte(ModRM);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.mov_rr8 : cycles.mov_rm8;
/*TODO*///    RegByte(ModRM)=src;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_r16w)(void)    /* Opcode 0x8b */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///	WORD src = GetRMWord(ModRM);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.mov_rr8 : cycles.mov_rm16;
/*TODO*///	RegWord(ModRM)=src;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_wsreg)(void)    /* Opcode 0x8c */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.mov_rs : cycles.mov_ms;
/*TODO*///#ifdef I286
/*TODO*///	if (ModRM & 0x20) {	/* HJB 12/13/98 1xx is invalid */
/*TODO*///		i286_trap2(ILLEGAL_INSTRUCTION);
/*TODO*///		return;
/*TODO*///	}
/*TODO*///#else
/*TODO*///	if (ModRM & 0x20) return;	/* HJB 12/13/98 1xx is invalid */
/*TODO*///#endif
/*TODO*///	PutRMWord(ModRM,I.sregs[(ModRM & 0x38) >> 3]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_lea)(void)    /* Opcode 0x8d */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.lea;
/*TODO*///	(void)(*GetEA[ModRM])();
/*TODO*///	RegWord(ModRM)=EO;	/* HJB 12/13/98 effective offset (no segment part) */
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_sregw)(void)    /* Opcode 0x8e */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///    WORD src = GetRMWord(ModRM);
/*TODO*///
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.mov_sr : cycles.mov_sm;
/*TODO*///#ifdef I286
/*TODO*///    switch (ModRM & 0x38)
/*TODO*///    {
/*TODO*///    case 0x00:  /* mov es,ew */
/*TODO*///		i286_data_descriptor(ES,src);
/*TODO*///		break;
/*TODO*///    case 0x18:  /* mov ds,ew */
/*TODO*///		i286_data_descriptor(DS,src);
/*TODO*///		break;
/*TODO*///    case 0x10:  /* mov ss,ew */
/*TODO*///		i286_data_descriptor(SS,src);
/*TODO*///		PREFIX(_instruction)[FETCHOP]();
/*TODO*///		break;
/*TODO*///    case 0x08:  /* mov cs,ew */
/*TODO*///		break;  /* doesn't do a jump far */
/*TODO*///    }
/*TODO*///#else
/*TODO*///    switch (ModRM & 0x38)
/*TODO*///    {
/*TODO*///    case 0x00:  /* mov es,ew */
/*TODO*///		I.sregs[ES] = src;
/*TODO*///		I.base[ES] = SegBase(ES);
/*TODO*///		break;
/*TODO*///    case 0x18:  /* mov ds,ew */
/*TODO*///		I.sregs[DS] = src;
/*TODO*///		I.base[DS] = SegBase(DS);
/*TODO*///		break;
/*TODO*///    case 0x10:  /* mov ss,ew */
/*TODO*///		I.sregs[SS] = src;
/*TODO*///		I.base[SS] = SegBase(SS); /* no interrupt allowed before next instr */
/*TODO*///		PREFIX(_instruction)[FETCHOP]();
/*TODO*///		break;
/*TODO*///    case 0x08:  /* mov cs,ew */
/*TODO*///		break;  /* doesn't do a jump far */
/*TODO*///    }
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_popw)(void)    /* Opcode 0x8f */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///    WORD tmp;
/*TODO*///	POP(tmp);
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.pop_r16 : cycles.pop_m16;
/*TODO*///	PutRMWord(ModRM,tmp);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///#define XchgAXReg(Reg) 				\
/*TODO*///{ 									\
/*TODO*///    WORD tmp; 						\
/*TODO*///	tmp = I.regs.w[Reg]; 			\
/*TODO*///	I.regs.w[Reg] = I.regs.w[AX]; 	\
/*TODO*///	I.regs.w[AX] = tmp; 			\
/*TODO*///	i86_ICount[0] -= cycles.xchg_ar16; 	\
/*TODO*///}
/*TODO*///
/*TODO*///
    static InstructionPtr i86_nop = new InstructionPtr() /* Opcode 0x90 */ {
        public void handler() {
            /* this is XchgAXReg(AX); */
            i86_ICount[0] -= cycles.nop;
        }
    };

    /*TODO*///
/*TODO*///static void PREFIX86(_xchg_axcx)(void)    /* Opcode 0x91 */
/*TODO*///{
/*TODO*///	XchgAXReg(CX);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xchg_axdx)(void)    /* Opcode 0x92 */
/*TODO*///{
/*TODO*///    XchgAXReg(DX);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xchg_axbx)(void)    /* Opcode 0x93 */
/*TODO*///{
/*TODO*///	XchgAXReg(BX);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xchg_axsp)(void)    /* Opcode 0x94 */
/*TODO*///{
/*TODO*///	XchgAXReg(SP);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xchg_axbp)(void)    /* Opcode 0x95 */
/*TODO*///{
/*TODO*///    XchgAXReg(BP);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xchg_axsi)(void)    /* Opcode 0x96 */
/*TODO*///{
/*TODO*///	XchgAXReg(SI);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_xchg_axdi)(void)    /* Opcode 0x97 */
/*TODO*///{
/*TODO*///	XchgAXReg(DI);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_cbw)(void)    /* Opcode 0x98 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.cbw;
/*TODO*///	I.regs.b[AH] = (I.regs.b[AL] & 0x80) ? 0xff : 0;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_cwd)(void)    /* Opcode 0x99 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.cwd;
/*TODO*///	I.regs.w[DX] = (I.regs.b[AH] & 0x80) ? 0xffff : 0;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_call_far)(void)
/*TODO*///{
/*TODO*///    unsigned tmp, tmp2;
/*TODO*///	WORD ip;
/*TODO*///
/*TODO*///	tmp = FETCH;
/*TODO*///	tmp += FETCH << 8;
/*TODO*///
/*TODO*///	tmp2 = FETCH;
/*TODO*///	tmp2 += FETCH << 8;
/*TODO*///
/*TODO*///	ip = I.pc - I.base[CS];
/*TODO*///	PUSH(I.sregs[CS]);
/*TODO*///	PUSH(ip);
/*TODO*///
/*TODO*///#ifdef I286
/*TODO*///	i286_code_descriptor(tmp2, tmp);
/*TODO*///#else
/*TODO*///	I.sregs[CS] = (WORD)tmp2;
/*TODO*///	I.base[CS] = SegBase(CS);
/*TODO*///	I.pc = (I.base[CS] + (WORD)tmp) & AMASK;
/*TODO*///#endif
/*TODO*///	i86_ICount[0] -= cycles.call_far;
/*TODO*///	CHANGE_PC(I.pc);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_wait)(void)    /* Opcode 0x9b */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.wait;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_pushf)(void)    /* Opcode 0x9c */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.pushf;
/*TODO*///#ifdef I286
/*TODO*///    PUSH( CompressFlags() | 0xc000 );
/*TODO*///#elif defined V20
/*TODO*///    PUSH( CompressFlags() | 0xe000 );
/*TODO*///#else
/*TODO*///    PUSH( CompressFlags() | 0xf000 );
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_popf)(void)    /* Opcode 0x9d */
/*TODO*///{
/*TODO*///	unsigned tmp;
/*TODO*///    POP(tmp);
/*TODO*///	i86_ICount[0] -= cycles.popf;
/*TODO*///    ExpandFlags(tmp);
/*TODO*///	
/*TODO*///	if (I.TF) PREFIX(_trap)();
/*TODO*///
/*TODO*///	/* if the IF is set, and an interrupt is pending, signal an interrupt */
/*TODO*///	if (I.IF && I.irq_state)
/*TODO*///#ifdef V20
/*TODO*///		PREFIX(_interrupt)(-1, 0);
/*TODO*///#else
/*TODO*///		PREFIX(_interrupt)(-1);
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_sahf)(void)    /* Opcode 0x9e */
/*TODO*///{
/*TODO*///	unsigned tmp = (CompressFlags() & 0xff00) | (I.regs.b[AH] & 0xd5);
/*TODO*///	i86_ICount[0] -= cycles.sahf;
/*TODO*///    ExpandFlags(tmp);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_lahf)(void)    /* Opcode 0x9f */
/*TODO*///{
/*TODO*///	I.regs.b[AH] = CompressFlags() & 0xff;
/*TODO*///	i86_ICount[0] -= cycles.lahf;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_mov_aldisp)(void)    /* Opcode 0xa0 */
/*TODO*///{
/*TODO*///	unsigned addr;
/*TODO*///
/*TODO*///	addr = FETCH;
/*TODO*///	addr += FETCH << 8;
/*TODO*///
/*TODO*///	i86_ICount[0] -= cycles.mov_am8;
/*TODO*///	I.regs.b[AL] = GetMemB(DS, addr);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_axdisp)(void)    /* Opcode 0xa1 */
/*TODO*///{
/*TODO*///	unsigned addr;
/*TODO*///
/*TODO*///	addr = FETCH;
/*TODO*///	addr += FETCH << 8;
/*TODO*///	
/*TODO*///	i86_ICount[0] -= cycles.mov_am16;
/*TODO*///	I.regs.b[AL] = GetMemB(DS, addr);
/*TODO*///	I.regs.b[AH] = GetMemB(DS, addr+1);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_dispal)(void)    /* Opcode 0xa2 */
/*TODO*///{
/*TODO*///    unsigned addr;
/*TODO*///	
/*TODO*///	addr = FETCH;
/*TODO*///	addr += FETCH << 8;
/*TODO*///	
/*TODO*///	i86_ICount[0] -= cycles.mov_ma8;
/*TODO*///	PutMemB(DS, addr, I.regs.b[AL]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_dispax)(void)    /* Opcode 0xa3 */
/*TODO*///{
/*TODO*///	unsigned addr;
/*TODO*///	
/*TODO*///	addr = FETCH;
/*TODO*///	addr += FETCH << 8;
/*TODO*///
/*TODO*///	i86_ICount[0] -= cycles.mov_ma16;
/*TODO*///	PutMemB(DS, addr, I.regs.b[AL]);
/*TODO*///	PutMemB(DS, addr+1, I.regs.b[AH]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_movsb)(void)    /* Opcode 0xa4 */
/*TODO*///{
/*TODO*///	BYTE tmp = GetMemB(DS,I.regs.w[SI]);
/*TODO*///	PutMemB(ES,I.regs.w[DI], tmp);
/*TODO*///	I.regs.w[DI] += I.DirVal;
/*TODO*///	I.regs.w[SI] += I.DirVal;
/*TODO*///	i86_ICount[0] -= cycles.movs8;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_movsw)(void)    /* Opcode 0xa5 */
/*TODO*///{
/*TODO*///	WORD tmp = GetMemW(DS,I.regs.w[SI]);
/*TODO*///	PutMemW(ES,I.regs.w[DI], tmp);
/*TODO*///	I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///	I.regs.w[SI] += 2 * I.DirVal;
/*TODO*///	i86_ICount[0] -= cycles.movs16;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_cmpsb)(void)    /* Opcode 0xa6 */
/*TODO*///{
/*TODO*///	unsigned dst = GetMemB(ES, I.regs.w[DI]);
/*TODO*///	unsigned src = GetMemB(DS, I.regs.w[SI]);
/*TODO*///    SUBB(src,dst); /* opposite of the usual convention */
/*TODO*///	I.regs.w[DI] += I.DirVal;
/*TODO*///	I.regs.w[SI] += I.DirVal;
/*TODO*///	i86_ICount[0] -= cycles.cmps8;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_cmpsw)(void)    /* Opcode 0xa7 */
/*TODO*///{
/*TODO*///	unsigned dst = GetMemW(ES, I.regs.w[DI]);
/*TODO*///	unsigned src = GetMemW(DS, I.regs.w[SI]);
/*TODO*///	SUBW(src,dst); /* opposite of the usual convention */
/*TODO*///	I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///	I.regs.w[SI] += 2 * I.DirVal;
/*TODO*///	i86_ICount[0] -= cycles.cmps16;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_test_ald8)(void)    /* Opcode 0xa8 */
/*TODO*///{
/*TODO*///    DEF_ald8(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri8;
/*TODO*///    ANDB(dst,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_test_axd16)(void)    /* Opcode 0xa9 */
/*TODO*///{
/*TODO*///    DEF_axd16(dst,src);
/*TODO*///	i86_ICount[0] -= cycles.alu_ri16;
/*TODO*///    ANDW(dst,src);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_stosb)(void)    /* Opcode 0xaa */
/*TODO*///{
/*TODO*///	PutMemB(ES,I.regs.w[DI],I.regs.b[AL]);
/*TODO*///	I.regs.w[DI] += I.DirVal;
/*TODO*///	i86_ICount[0] -= cycles.stos8;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_stosw)(void)    /* Opcode 0xab */
/*TODO*///{
/*TODO*///	PutMemB(ES,I.regs.w[DI],I.regs.b[AL]);
/*TODO*///	PutMemB(ES,I.regs.w[DI]+1,I.regs.b[AH]);
/*TODO*///	I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///	i86_ICount[0] -= cycles.stos16;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_lodsb)(void)    /* Opcode 0xac */
/*TODO*///{
/*TODO*///	I.regs.b[AL] = GetMemB(DS,I.regs.w[SI]);
/*TODO*///	I.regs.w[SI] += I.DirVal;
/*TODO*///	i86_ICount[0] -= cycles.lods8;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_lodsw)(void)    /* Opcode 0xad */
/*TODO*///{
/*TODO*///	I.regs.w[AX] = GetMemW(DS,I.regs.w[SI]);
/*TODO*///	I.regs.w[SI] += 2 * I.DirVal;
/*TODO*///	i86_ICount[0] -= cycles.lods16;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_scasb)(void)    /* Opcode 0xae */
/*TODO*///{
/*TODO*///	unsigned src = GetMemB(ES, I.regs.w[DI]);
/*TODO*///	unsigned dst = I.regs.b[AL];
/*TODO*///    SUBB(dst,src);
/*TODO*///	I.regs.w[DI] += I.DirVal;
/*TODO*///	i86_ICount[0] -= cycles.scas8;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_scasw)(void)    /* Opcode 0xaf */
/*TODO*///{
/*TODO*///	unsigned src = GetMemW(ES, I.regs.w[DI]);
/*TODO*///	unsigned dst = I.regs.w[AX];
/*TODO*///    SUBW(dst,src);
/*TODO*///	I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///	i86_ICount[0] -= cycles.scas16;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_ald8)(void)    /* Opcode 0xb0 */
/*TODO*///{
/*TODO*///	I.regs.b[AL] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri8;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_cld8)(void)    /* Opcode 0xb1 */
/*TODO*///{
/*TODO*///	I.regs.b[CL] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri8;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_dld8)(void)    /* Opcode 0xb2 */
/*TODO*///{
/*TODO*///	I.regs.b[DL] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri8;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_bld8)(void)    /* Opcode 0xb3 */
/*TODO*///{
/*TODO*///	I.regs.b[BL] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri8;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_ahd8)(void)    /* Opcode 0xb4 */
/*TODO*///{
/*TODO*///	I.regs.b[AH] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri8;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_chd8)(void)    /* Opcode 0xb5 */
/*TODO*///{
/*TODO*///	I.regs.b[CH] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri8;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_dhd8)(void)    /* Opcode 0xb6 */
/*TODO*///{
/*TODO*///	I.regs.b[DH] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri8;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_bhd8)(void)    /* Opcode 0xb7 */
/*TODO*///{
/*TODO*///	I.regs.b[BH] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri8;
/*TODO*///}
/*TODO*///
    static InstructionPtr i86_mov_axd16 = new InstructionPtr() /* Opcode 0xb8 */ {
        public void handler() {
            I.regs.SetB(AL, FETCH());//I.regs.b[AL] = FETCH;
            I.regs.SetB(AH, FETCH());//I.regs.b[AH] = FETCH;
            i86_ICount[0] -= cycles.mov_ri16;
        }
    };

    /*TODO*///
/*TODO*///static void PREFIX86(_mov_cxd16)(void)    /* Opcode 0xb9 */
/*TODO*///{
/*TODO*///	I.regs.b[CL] = FETCH;
/*TODO*///	I.regs.b[CH] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri16;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_dxd16)(void)    /* Opcode 0xba */
/*TODO*///{
/*TODO*///	I.regs.b[DL] = FETCH;
/*TODO*///	I.regs.b[DH] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri16;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_bxd16)(void)    /* Opcode 0xbb */
/*TODO*///{
/*TODO*///	I.regs.b[BL] = FETCH;
/*TODO*///	I.regs.b[BH] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri16;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_spd16)(void)    /* Opcode 0xbc */
/*TODO*///{
/*TODO*///	I.regs.b[SPL] = FETCH;
/*TODO*///	I.regs.b[SPH] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri16;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_bpd16)(void)    /* Opcode 0xbd */
/*TODO*///{
/*TODO*///	I.regs.b[BPL] = FETCH;
/*TODO*///	I.regs.b[BPH] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri16;
/*TODO*///}
/*TODO*///
    static void i186_mov_sid16() /* Opcode 0xbe */ {
        I.regs.SetB(SIL, FETCH());
        I.regs.SetB(SIH, FETCH());
        i86_ICount[0] -= cycles.mov_ri16;
    }

    /*TODO*///
/*TODO*///static void PREFIX86(_mov_did16)(void)    /* Opcode 0xbf */
/*TODO*///{
/*TODO*///	I.regs.b[DIL] = FETCH;
/*TODO*///	I.regs.b[DIH] = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.mov_ri16;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_ret_d16)(void)    /* Opcode 0xc2 */
/*TODO*///{
/*TODO*///	unsigned count = FETCH;
/*TODO*///	count += FETCH << 8;
/*TODO*///	POP(I.pc);
/*TODO*///	I.pc = (I.pc + I.base[CS]) & AMASK;
/*TODO*///	I.regs.w[SP]+=count;
/*TODO*///	i86_ICount[0] -= cycles.ret_near_imm;
/*TODO*///	CHANGE_PC(I.pc);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_ret)(void)    /* Opcode 0xc3 */
/*TODO*///{
/*TODO*///	POP(I.pc);
/*TODO*///	I.pc = (I.pc + I.base[CS]) & AMASK;
/*TODO*///	i86_ICount[0] -= cycles.ret_near;
/*TODO*///	CHANGE_PC(I.pc);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_les_dw)(void)    /* Opcode 0xc4 */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///    WORD tmp = GetRMWord(ModRM);
/*TODO*///	
/*TODO*///    RegWord(ModRM)= tmp;
/*TODO*///#ifdef I286
/*TODO*///	i286_data_descriptor(ES,GetnextRMWord);
/*TODO*///#else
/*TODO*///	I.sregs[ES] = GetnextRMWord;
/*TODO*///	I.base[ES] = SegBase(ES);
/*TODO*///#endif
/*TODO*///	i86_ICount[0] -= cycles.load_ptr;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_lds_dw)(void)    /* Opcode 0xc5 */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///    WORD tmp = GetRMWord(ModRM);
/*TODO*///
/*TODO*///    RegWord(ModRM)=tmp;
/*TODO*///#ifdef I286
/*TODO*///	i286_data_descriptor(DS,GetnextRMWord);
/*TODO*///#else
/*TODO*///	I.sregs[DS] = GetnextRMWord;
/*TODO*///	I.base[DS] = SegBase(DS);
/*TODO*///#endif
/*TODO*///	i86_ICount[0] -= cycles.load_ptr;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_bd8)(void)    /* Opcode 0xc6 */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.mov_ri8 : cycles.mov_mi8;
/*TODO*///	PutImmRMByte(ModRM);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_mov_wd16)(void)    /* Opcode 0xc7 */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.mov_ri16 : cycles.mov_mi16;
/*TODO*///	PutImmRMWord(ModRM);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_retf_d16)(void)    /* Opcode 0xca */
/*TODO*///{
/*TODO*///	unsigned count = FETCH;
/*TODO*///	count += FETCH << 8;
/*TODO*///
/*TODO*///#ifdef I286
/*TODO*///	{ 
/*TODO*///		int tmp, tmp2;
/*TODO*///		POP(tmp2);
/*TODO*///		POP(tmp);
/*TODO*///		i286_code_descriptor(tmp, tmp2);
/*TODO*///	}
/*TODO*///#else
/*TODO*///	POP(I.pc);
/*TODO*///	POP(I.sregs[CS]);
/*TODO*///	I.base[CS] = SegBase(CS);
/*TODO*///	I.pc = (I.pc + I.base[CS]) & AMASK;
/*TODO*///#endif
/*TODO*///	I.regs.w[SP]+=count;
/*TODO*///	i86_ICount[0] -= cycles.ret_far_imm;
/*TODO*///	CHANGE_PC(I.pc);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_retf)(void)    /* Opcode 0xcb */
/*TODO*///{
/*TODO*///#ifdef I286
/*TODO*///	{ 
/*TODO*///		int tmp, tmp2;
/*TODO*///		POP(tmp2);
/*TODO*///		POP(tmp);
/*TODO*///		i286_code_descriptor(tmp, tmp2);
/*TODO*///	}
/*TODO*///#else
/*TODO*///	POP(I.pc);
/*TODO*///	POP(I.sregs[CS]);
/*TODO*///	I.base[CS] = SegBase(CS);
/*TODO*///	I.pc = (I.pc + I.base[CS]) & AMASK;
/*TODO*///#endif
/*TODO*///	i86_ICount[0] -= cycles.ret_far;
/*TODO*///	CHANGE_PC(I.pc);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_int3)(void)    /* Opcode 0xcc */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.int3;
/*TODO*///#ifdef V20
/*TODO*///	PREFIX(_interrupt)(3,0);
/*TODO*///#else
/*TODO*///	PREFIX(_interrupt)(3);
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_int)(void)    /* Opcode 0xcd */
/*TODO*///{
/*TODO*///	unsigned int_num = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.int_imm;
/*TODO*///#ifdef V20
/*TODO*///	PREFIX(_interrupt)(int_num,0);
/*TODO*///#else
/*TODO*///	PREFIX(_interrupt)(int_num);
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_into)(void)    /* Opcode 0xce */
/*TODO*///{
/*TODO*///	if (OF) {
/*TODO*///		i86_ICount[0] -= cycles.into_t;
/*TODO*///#ifdef V20
/*TODO*///		PREFIX(_interrupt)(4,0);
/*TODO*///#else
/*TODO*///		PREFIX(_interrupt)(4);
/*TODO*///#endif
/*TODO*///	} else i86_ICount[0] -= cycles.into_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_iret)(void)    /* Opcode 0xcf */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.iret;
/*TODO*///#ifdef I286
/*TODO*///	{ 
/*TODO*///		int tmp, tmp2;
/*TODO*///		POP(tmp2);
/*TODO*///		POP(tmp);
/*TODO*///		i286_code_descriptor(tmp, tmp2);
/*TODO*///	}
/*TODO*///#else
/*TODO*///	POP(I.pc);
/*TODO*///	POP(I.sregs[CS]);
/*TODO*///	I.base[CS] = SegBase(CS);
/*TODO*///	I.pc = (I.pc + I.base[CS]) & AMASK;
/*TODO*///#endif
/*TODO*///    PREFIX(_popf)();
/*TODO*///	CHANGE_PC(I.pc);
/*TODO*///
/*TODO*///	/* if the IF is set, and an interrupt is pending, signal an interrupt */
/*TODO*///	if (I.IF && I.irq_state)
/*TODO*///#ifdef V20
/*TODO*///		PREFIX(_interrupt)(-1, 0);
/*TODO*///#else
/*TODO*///		PREFIX(_interrupt)(-1);
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_rotshft_b)(void)    /* Opcode 0xd0 */
/*TODO*///{
/*TODO*///	PREFIX(_rotate_shift_Byte)(FETCHOP,1);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_rotshft_w)(void)    /* Opcode 0xd1 */
/*TODO*///{
/*TODO*///	PREFIX(_rotate_shift_Word)(FETCHOP,1);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_rotshft_bcl)(void)    /* Opcode 0xd2 */
/*TODO*///{
/*TODO*///	PREFIX(_rotate_shift_Byte)(FETCHOP,I.regs.b[CL]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_rotshft_wcl)(void)    /* Opcode 0xd3 */
/*TODO*///{
/*TODO*///	PREFIX(_rotate_shift_Word)(FETCHOP,I.regs.b[CL]);
/*TODO*///}
/*TODO*///
/*TODO*////* OB: Opcode works on NEC V-Series but not the Variants              */
/*TODO*////*     one could specify any byte value as operand but the NECs */
/*TODO*////*     always substitute 0x0a.              */
/*TODO*///static void PREFIX86(_aam)(void)    /* Opcode 0xd4 */
/*TODO*///{
/*TODO*///	unsigned mult = FETCH;
/*TODO*///
/*TODO*///	i86_ICount[0] -= cycles.aam;
/*TODO*///#ifndef V20
/*TODO*///	if (mult == 0)
/*TODO*///		PREFIX(_interrupt)(0);
/*TODO*///	else
/*TODO*///	{
/*TODO*///		I.regs.b[AH] = I.regs.b[AL] / mult;
/*TODO*///		I.regs.b[AL] %= mult;
/*TODO*///
/*TODO*///		SetSZPF_Word(I.regs.w[AX]);
/*TODO*///	}
/*TODO*///#else
/*TODO*/// 
/*TODO*///	if (mult == 0) 
/*TODO*///		PREFIX(_interrupt)(0,0); 
/*TODO*///    else 
/*TODO*///    { 
/*TODO*///		I.regs.b[AH] = I.regs.b[AL] / 10; 
/*TODO*///		I.regs.b[AL] %= 10; 
/*TODO*///		SetSZPF_Word(I.regs.w[AX]); 
/*TODO*///    } 
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_aad)(void)    /* Opcode 0xd5 */
/*TODO*///{
/*TODO*///	unsigned mult = FETCH;
/*TODO*///
/*TODO*///	i86_ICount[0] -= cycles.aad;
/*TODO*///
/*TODO*///#ifndef V20
/*TODO*///	I.regs.b[AL] = I.regs.b[AH] * mult + I.regs.b[AL];
/*TODO*///	I.regs.b[AH] = 0;
/*TODO*///
/*TODO*///	SetZF(I.regs.b[AL]);
/*TODO*///	SetPF(I.regs.b[AL]);
/*TODO*///	I.SignVal = 0;
/*TODO*///#else
/*TODO*////* OB: Opcode works on NEC V-Series but not the Variants 	*/ 
/*TODO*////*     one could specify any byte value as operand but the NECs */ 
/*TODO*////*     always substitute 0x0a.					*/ 
/*TODO*///	I.regs.b[AL] = I.regs.b[AH] * 10 + I.regs.b[AL]; 
/*TODO*///	I.regs.b[AH] = 0; 
/*TODO*/// 
/*TODO*///	SetZF(I.regs.b[AL]); 
/*TODO*///	SetPF(I.regs.b[AL]); 
/*TODO*///	I.SignVal = 0; 
/*TODO*///	mult=0; 
/*TODO*///#endif
/*TODO*///} 
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_xlat)(void)    /* Opcode 0xd7 */
/*TODO*///{
/*TODO*///	unsigned dest = I.regs.w[BX]+I.regs.b[AL];
/*TODO*///
/*TODO*///	i86_ICount[0] -= cycles.xlat;
/*TODO*///	I.regs.b[AL] = GetMemB(DS, dest);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_escape)(void)    /* Opcodes 0xd8, 0xd9, 0xda, 0xdb, 0xdc, 0xdd, 0xde and 0xdf */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///	i86_ICount[0] -= cycles.nop;
/*TODO*///    GetRMByte(ModRM);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_loopne)(void)    /* Opcode 0xe0 */
/*TODO*///{
/*TODO*///	int disp = (int)((INT8)FETCH);
/*TODO*///	unsigned tmp = I.regs.w[CX]-1;
/*TODO*///
/*TODO*///	I.regs.w[CX]=tmp;
/*TODO*///
/*TODO*///    if (!ZF && tmp) {
/*TODO*///		i86_ICount[0] -= cycles.loop_t;
/*TODO*///		I.pc += disp;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.loop_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_loope)(void)    /* Opcode 0xe1 */
/*TODO*///{
/*TODO*///	int disp = (int)((INT8)FETCH);
/*TODO*///	unsigned tmp = I.regs.w[CX]-1;
/*TODO*///
/*TODO*///	I.regs.w[CX]=tmp;
/*TODO*///
/*TODO*///	if (ZF && tmp) {
/*TODO*///		i86_ICount[0] -= cycles.loope_t;
/*TODO*///		 I.pc += disp;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		 CHANGE_PC(I.pc);*/
/*TODO*///	 } else i86_ICount[0] -= cycles.loope_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_loop)(void)    /* Opcode 0xe2 */
/*TODO*///{
/*TODO*///	int disp = (int)((INT8)FETCH);
/*TODO*///	unsigned tmp = I.regs.w[CX]-1;
/*TODO*///
/*TODO*///	I.regs.w[CX]=tmp;
/*TODO*///
/*TODO*///    if (tmp) {
/*TODO*///		i86_ICount[0] -= cycles.loop_t;
/*TODO*///		I.pc += disp;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else i86_ICount[0] -= cycles.loop_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jcxz)(void)    /* Opcode 0xe3 */
/*TODO*///{
/*TODO*///	int disp = (int)((INT8)FETCH);
/*TODO*///
/*TODO*///	if (I.regs.w[CX] == 0) {
/*TODO*///		i86_ICount[0] -= cycles.jcxz_t;
/*TODO*///		I.pc += disp;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///		CHANGE_PC(I.pc);*/
/*TODO*///	} else 
/*TODO*///		i86_ICount[0] -= cycles.jcxz_nt;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_inal)(void)    /* Opcode 0xe4 */
/*TODO*///{
/*TODO*///	unsigned port = FETCH;
/*TODO*///
/*TODO*///	i86_ICount[0] -= cycles.in_imm8;
/*TODO*///	I.regs.b[AL] = read_port(port);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_inax)(void)    /* Opcode 0xe5 */
/*TODO*///{
/*TODO*///	unsigned port = FETCH;
/*TODO*///
/*TODO*///	i86_ICount[0] -= cycles.in_imm16;
/*TODO*///	I.regs.b[AL] = read_port(port);
/*TODO*///	I.regs.b[AH] = read_port(port+1);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_outal)(void)    /* Opcode 0xe6 */
/*TODO*///{
/*TODO*///	unsigned port = FETCH;
/*TODO*///
/*TODO*///	i86_ICount[0] -= cycles.out_imm8;
/*TODO*///	write_port(port, I.regs.b[AL]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_outax)(void)    /* Opcode 0xe7 */
/*TODO*///{
/*TODO*///	unsigned port = FETCH;
/*TODO*///
/*TODO*///	i86_ICount[0] -= cycles.out_imm16;
/*TODO*///	write_port(port, I.regs.b[AL]);
/*TODO*///	write_port(port+1, I.regs.b[AH]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_call_d16)(void)    /* Opcode 0xe8 */
/*TODO*///{
/*TODO*///	WORD ip, tmp;
/*TODO*///	
/*TODO*///	FETCHWORD(tmp);
/*TODO*///	ip = I.pc - I.base[CS];
/*TODO*///	PUSH(ip);
/*TODO*///	ip += tmp;
/*TODO*///	I.pc = (ip + I.base[CS]) & AMASK;
/*TODO*///	i86_ICount[0] -= cycles.call_near;
/*TODO*///	CHANGE_PC(I.pc);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_jmp_d16)(void)    /* Opcode 0xe9 */
/*TODO*///{
/*TODO*///	WORD ip, tmp;
/*TODO*///	
/*TODO*///	FETCHWORD(tmp);
/*TODO*///	ip = I.pc - I.base[CS] + tmp;
/*TODO*///	I.pc = (ip + I.base[CS]) & AMASK;
/*TODO*///	i86_ICount[0] -= cycles.jmp_near;
/*TODO*///	CHANGE_PC(I.pc);
/*TODO*///}
/*TODO*///
    static InstructionPtr i86_jmp_far = new InstructionPtr() /* Opcode 0xea */ {
        public void handler() {
            int/*unsigned*/ tmp, tmp1;

            tmp = FETCH();
            tmp += FETCH() << 8;

            tmp1 = FETCH();
            tmp1 += FETCH() << 8;

            /*TODO*///#ifdef I286
/*TODO*///	i286_code_descriptor(tmp1,tmp);
/*TODO*///#else
            I.sregs[CS] = tmp1 & 0xFFFF;
            I.base[CS] = SegBase(CS);
            I.pc = (I.base[CS] + tmp) & AMASK;
            /*TODO*///#endif
            i86_ICount[0] -= cycles.jmp_far;
            change_pc20(I.pc);
        }
    };

    /*TODO*///
/*TODO*///static void PREFIX86(_jmp_d8)(void)    /* Opcode 0xeb */
/*TODO*///{
/*TODO*///	int tmp = (int)((INT8)FETCH);
/*TODO*///	I.pc += tmp;
/*TODO*////* ASG - can probably assume this is safe
/*TODO*///	CHANGE_PC(I.pc);*/
/*TODO*///	i86_ICount[0] -= cycles.jmp_short;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_inaldx)(void)    /* Opcode 0xec */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.in_dx8;
/*TODO*///	I.regs.b[AL] = read_port(I.regs.w[DX]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_inaxdx)(void)    /* Opcode 0xed */
/*TODO*///{
/*TODO*///	unsigned port = I.regs.w[DX];
/*TODO*///
/*TODO*///	i86_ICount[0] -= cycles.in_dx16;
/*TODO*///	I.regs.b[AL] = read_port(port);
/*TODO*///	I.regs.b[AH] = read_port(port+1);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_outdxal)(void)    /* Opcode 0xee */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.out_dx8;
/*TODO*///	write_port(I.regs.w[DX], I.regs.b[AL]);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_outdxax)(void)    /* Opcode 0xef */
/*TODO*///{
/*TODO*///	unsigned port = I.regs.w[DX];
/*TODO*///
/*TODO*///	i86_ICount[0] -= cycles.out_dx16;
/*TODO*///	write_port(port, I.regs.b[AL]);
/*TODO*///	write_port(port+1, I.regs.b[AH]);
/*TODO*///}
/*TODO*///
/*TODO*////* I think thats not a V20 instruction...*/
/*TODO*///static void PREFIX86(_lock)(void)    /* Opcode 0xf0 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.nop;
/*TODO*///	PREFIX(_instruction)[FETCHOP]();  /* un-interruptible */
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///static void PREFIX(_repne)(void)    /* Opcode 0xf2 */
/*TODO*///{
/*TODO*///	 PREFIX(rep)(0);
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX(_repe)(void)    /* Opcode 0xf3 */
/*TODO*///{
/*TODO*///	 PREFIX(rep)(1);
/*TODO*///}
/*TODO*///
/*TODO*///#ifndef I186
/*TODO*///static void PREFIX86(_hlt)(void)    /* Opcode 0xf4 */
/*TODO*///{
/*TODO*///	I.pc--;
/*TODO*///	i86_ICount[0] = 0;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_cmc)(void)    /* Opcode 0xf5 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.flag_ops;
/*TODO*///	I.CarryVal = !CF;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_f6pre)(void)
/*TODO*///{
/*TODO*///	/* Opcode 0xf6 */
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///    unsigned tmp = (unsigned)GetRMByte(ModRM);
/*TODO*///    unsigned tmp2;
/*TODO*///
/*TODO*///
/*TODO*///    switch (ModRM & 0x38)
/*TODO*///    {
/*TODO*///    case 0x00:  /* TEST Eb, data8 */
/*TODO*///    case 0x08:  /* ??? */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri8 : cycles.alu_mi8_ro;
/*TODO*///		tmp &= FETCH;
/*TODO*///
/*TODO*///		I.CarryVal = I.OverVal = I.AuxVal = 0;
/*TODO*///		SetSZPF_Byte(tmp);
/*TODO*///		break;
/*TODO*///
/*TODO*///    case 0x10:  /* NOT Eb */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.negnot_r8 : cycles.negnot_m8;
/*TODO*///		PutbackRMByte(ModRM,~tmp);
/*TODO*///		break;
/*TODO*///
/*TODO*///	 case 0x18:  /* NEG Eb */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.negnot_r8 : cycles.negnot_m8;
/*TODO*///        tmp2=0;
/*TODO*///        SUBB(tmp2,tmp);
/*TODO*///        PutbackRMByte(ModRM,tmp2);
/*TODO*///		break;
/*TODO*///    case 0x20:  /* MUL AL, Eb */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.mul_r8 : cycles.mul_m8;
/*TODO*///		{
/*TODO*///			UINT16 result;
/*TODO*///			tmp2 = I.regs.b[AL];
/*TODO*///
/*TODO*///			SetSF((INT8)tmp2);
/*TODO*///			SetPF(tmp2);
/*TODO*///
/*TODO*///			result = (UINT16)tmp2*tmp;
/*TODO*///			I.regs.w[AX]=(WORD)result;
/*TODO*///
/*TODO*///			SetZF(I.regs.w[AX]);
/*TODO*///			I.CarryVal = I.OverVal = (I.regs.b[AH] != 0);
/*TODO*///		}
/*TODO*///		break;
/*TODO*///	 case 0x28:  /* IMUL AL, Eb */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.imul_r8 : cycles.imul_m8;
/*TODO*///		{
/*TODO*///			INT16 result;
/*TODO*///
/*TODO*///			tmp2 = (unsigned)I.regs.b[AL];
/*TODO*///
/*TODO*///			SetSF((INT8)tmp2);
/*TODO*///			SetPF(tmp2);
/*TODO*///
/*TODO*///			result = (INT16)((INT8)tmp2)*(INT16)((INT8)tmp);
/*TODO*///			I.regs.w[AX]=(WORD)result;
/*TODO*///
/*TODO*///			SetZF(I.regs.w[AX]);
/*TODO*///
/*TODO*///			I.CarryVal = I.OverVal = (result >> 7 != 0) && (result >> 7 != -1);
/*TODO*///		}
/*TODO*///		break;
/*TODO*///    case 0x30:  /* DIV AL, Ew */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.div_r8 : cycles.div_m8;
/*TODO*///		{
/*TODO*///			UINT16 result;
/*TODO*///
/*TODO*///			result = I.regs.w[AX];
/*TODO*///
/*TODO*///			if (tmp)
/*TODO*///			{
/*TODO*///				if ((result / tmp) > 0xff)
/*TODO*///				{
/*TODO*///#ifdef V20
/*TODO*///					PREFIX(_interrupt)(0,0);
/*TODO*///#else
/*TODO*///					PREFIX(_interrupt)(0);
/*TODO*///#endif
/*TODO*///					break;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					I.regs.b[AH] = result % tmp;
/*TODO*///					I.regs.b[AL] = result / tmp;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///#ifdef V20
/*TODO*///				PREFIX(_interrupt)(0,0);
/*TODO*///#else
/*TODO*///				PREFIX(_interrupt)(0);
/*TODO*///#endif
/*TODO*///				break;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		break;
/*TODO*///    case 0x38:  /* IDIV AL, Ew */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.idiv_r8 : cycles.idiv_m8;
/*TODO*///		{
/*TODO*///
/*TODO*///			INT16 result;
/*TODO*///
/*TODO*///			result = I.regs.w[AX];
/*TODO*///
/*TODO*///			if (tmp)
/*TODO*///			{
/*TODO*///				tmp2 = result % (INT16)((INT8)tmp);
/*TODO*///
/*TODO*///				if ((result /= (INT16)((INT8)tmp)) > 0xff)
/*TODO*///				{
/*TODO*///#ifdef V20
/*TODO*///					PREFIX(_interrupt)(0,0);
/*TODO*///#else
/*TODO*///					PREFIX(_interrupt)(0);
/*TODO*///#endif
/*TODO*///					break;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					I.regs.b[AL] = result;
/*TODO*///					I.regs.b[AH] = tmp2;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///#ifdef V20
/*TODO*///				PREFIX(_interrupt)(0,0);
/*TODO*///#else
/*TODO*///				PREFIX(_interrupt)(0);
/*TODO*///#endif
/*TODO*///				break;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		break;
/*TODO*///    }
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_f7pre)(void)
/*TODO*///{
/*TODO*///	/* Opcode 0xf7 */
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///	 unsigned tmp = GetRMWord(ModRM);
/*TODO*///    unsigned tmp2;
/*TODO*///
/*TODO*///
/*TODO*///    switch (ModRM & 0x38)
/*TODO*///    {
/*TODO*///    case 0x00:  /* TEST Ew, data16 */
/*TODO*///    case 0x08:  /* ??? */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.alu_ri16 : cycles.alu_mi16_ro;
/*TODO*///		tmp2 = FETCH;
/*TODO*///		tmp2 += FETCH << 8;
/*TODO*///
/*TODO*///		tmp &= tmp2;
/*TODO*///
/*TODO*///		I.CarryVal = I.OverVal = I.AuxVal = 0;
/*TODO*///		SetSZPF_Word(tmp);
/*TODO*///		break;
/*TODO*///
/*TODO*///    case 0x10:  /* NOT Ew */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.negnot_r16 : cycles.negnot_m16;
/*TODO*///		tmp = ~tmp;
/*TODO*///		PutbackRMWord(ModRM,tmp);
/*TODO*///		break;
/*TODO*///
/*TODO*///    case 0x18:  /* NEG Ew */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.negnot_r16 : cycles.negnot_m16;
/*TODO*///        tmp2 = 0;
/*TODO*///        SUBW(tmp2,tmp);
/*TODO*///        PutbackRMWord(ModRM,tmp2);
/*TODO*///		break;
/*TODO*///    case 0x20:  /* MUL AX, Ew */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.mul_r16 : cycles.mul_m16;
/*TODO*///		{
/*TODO*///			UINT32 result;
/*TODO*///			tmp2 = I.regs.w[AX];
/*TODO*///
/*TODO*///			SetSF((INT16)tmp2);
/*TODO*///			SetPF(tmp2);
/*TODO*///
/*TODO*///			result = (UINT32)tmp2*tmp;
/*TODO*///			I.regs.w[AX]=(WORD)result;
/*TODO*///            result >>= 16;
/*TODO*///			I.regs.w[DX]=result;
/*TODO*///
/*TODO*///			SetZF(I.regs.w[AX] | I.regs.w[DX]);
/*TODO*///			I.CarryVal = I.OverVal = (I.regs.w[DX] != 0);
/*TODO*///		}
/*TODO*///		break;
/*TODO*///
/*TODO*///    case 0x28:  /* IMUL AX, Ew */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.imul_r16 : cycles.imul_m16;
/*TODO*///		{
/*TODO*///			INT32 result;
/*TODO*///
/*TODO*///			tmp2 = I.regs.w[AX];
/*TODO*///
/*TODO*///			SetSF((INT16)tmp2);
/*TODO*///			SetPF(tmp2);
/*TODO*///
/*TODO*///			result = (INT32)((INT16)tmp2)*(INT32)((INT16)tmp);
/*TODO*///			I.CarryVal = I.OverVal = (result >> 15 != 0) && (result >> 15 != -1);
/*TODO*///
/*TODO*///			I.regs.w[AX]=(WORD)result;
/*TODO*///			result = (WORD)(result >> 16);
/*TODO*///			I.regs.w[DX]=result;
/*TODO*///
/*TODO*///			SetZF(I.regs.w[AX] | I.regs.w[DX]);
/*TODO*///		}
/*TODO*///		break;
/*TODO*///	 case 0x30:  /* DIV AX, Ew */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.div_r16 : cycles.div_m16;
/*TODO*///		{
/*TODO*///			UINT32 result;
/*TODO*///
/*TODO*///			result = (I.regs.w[DX] << 16) + I.regs.w[AX];
/*TODO*///
/*TODO*///			if (tmp)
/*TODO*///			{
/*TODO*///				tmp2 = result % tmp;
/*TODO*///				if ((result / tmp) > 0xffff)
/*TODO*///				{
/*TODO*///#ifdef V20
/*TODO*///					PREFIX(_interrupt)(0,0);
/*TODO*///#else
/*TODO*///					PREFIX(_interrupt)(0);
/*TODO*///#endif
/*TODO*///					break;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					I.regs.w[DX]=tmp2;
/*TODO*///					result /= tmp;
/*TODO*///					I.regs.w[AX]=result;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///#ifdef V20
/*TODO*///				PREFIX(_interrupt)(0,0);
/*TODO*///#else
/*TODO*///				PREFIX(_interrupt)(0);
/*TODO*///#endif
/*TODO*///				break;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		break;
/*TODO*///    case 0x38:  /* IDIV AX, Ew */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.idiv_r16 : cycles.idiv_m16;
/*TODO*///		{
/*TODO*///			INT32 result;
/*TODO*///
/*TODO*///			result = (I.regs.w[DX] << 16) + I.regs.w[AX];
/*TODO*///
/*TODO*///			if (tmp)
/*TODO*///			{
/*TODO*///				tmp2 = result % (INT32)((INT16)tmp);
/*TODO*///				if ((result /= (INT32)((INT16)tmp)) > 0xffff)
/*TODO*///				{
/*TODO*///#ifdef V20
/*TODO*///					PREFIX(_interrupt)(0,0);
/*TODO*///#else
/*TODO*///					PREFIX(_interrupt)(0);
/*TODO*///#endif
/*TODO*///					break;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					I.regs.w[AX]=result;
/*TODO*///					I.regs.w[DX]=tmp2;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///#ifdef V20
/*TODO*///				PREFIX(_interrupt)(0,0);
/*TODO*///#else
/*TODO*///				PREFIX(_interrupt)(0);
/*TODO*///#endif
/*TODO*///				break;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		break;
/*TODO*///    }
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_clc)(void)    /* Opcode 0xf8 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.flag_ops;
/*TODO*///	I.CarryVal = 0;
/*TODO*///}
/*TODO*///
/*TODO*///static void PREFIX86(_stc)(void)    /* Opcode 0xf9 */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.flag_ops;
/*TODO*///	I.CarryVal = 1;
/*TODO*///}
/*TODO*///
    static InstructionPtr i86_cli = new InstructionPtr() /* Opcode 0xfa */ {
        public void handler() {
            i86_ICount[0] -= cycles.flag_ops;
            SetIF(0);
        }
    };

    /*TODO*///
/*TODO*///static void PREFIX86(_sti)(void)    /* Opcode 0xfb */
/*TODO*///{
/*TODO*///	i86_ICount[0] -= cycles.flag_ops;
/*TODO*///	SetIF(1);
/*TODO*///	PREFIX(_instruction)[FETCHOP](); /* no interrupt before next instruction */
/*TODO*///
/*TODO*///	/* if an interrupt is pending, signal an interrupt */
/*TODO*///	if (I.irq_state)
/*TODO*///#ifdef V20
/*TODO*///		PREFIX(_interrupt)(-1, 0);
/*TODO*///#else
/*TODO*///		PREFIX(_interrupt)(-1);
/*TODO*///#endif
/*TODO*///}
/*TODO*///
    static InstructionPtr i86_cld = new InstructionPtr() /* Opcode 0xfc */ {
        public void handler() {
            i86_ICount[0] -= cycles.flag_ops;
            SetDF(0);
        }
    };

    static InstructionPtr i86_std = new InstructionPtr() /* Opcode 0xfd */ {
        public void handler() {
            i86_ICount[0] -= cycles.flag_ops;
            SetDF(1);
        }
    };
    /*TODO*///
/*TODO*///static void PREFIX86(_fepre)(void)    /* Opcode 0xfe */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCH;
/*TODO*///	unsigned tmp = GetRMByte(ModRM);
/*TODO*///    unsigned tmp1;
/*TODO*///
/*TODO*///	i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.incdec_r8 : cycles.incdec_m8;
/*TODO*///    if ((ModRM & 0x38) == 0)  /* INC eb */
/*TODO*///	 {
/*TODO*///		tmp1 = tmp+1;
/*TODO*///		SetOFB_Add(tmp1,tmp,1);
/*TODO*///    }
/*TODO*///	 else  /* DEC eb */
/*TODO*///    {
/*TODO*///		tmp1 = tmp-1;
/*TODO*///		SetOFB_Sub(tmp1,1,tmp);
/*TODO*///    }
/*TODO*///
/*TODO*///    SetAF(tmp1,tmp,1);
/*TODO*///    SetSZPF_Byte(tmp1);
/*TODO*///
/*TODO*///    PutbackRMByte(ModRM,(BYTE)tmp1);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_ffpre)(void)    /* Opcode 0xff */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCHOP;
/*TODO*///    unsigned tmp;
/*TODO*///    unsigned tmp1;
/*TODO*///    WORD ip;
/*TODO*///
/*TODO*///    switch(ModRM & 0x38)
/*TODO*///    {
/*TODO*///    case 0x00:  /* INC ew */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.incdec_r16 : cycles.incdec_m16;
/*TODO*///		tmp = GetRMWord(ModRM);
/*TODO*///		tmp1 = tmp+1;
/*TODO*///
/*TODO*///		SetOFW_Add(tmp1,tmp,1);
/*TODO*///		SetAF(tmp1,tmp,1);
/*TODO*///		SetSZPF_Word(tmp1);
/*TODO*///
/*TODO*///		PutbackRMWord(ModRM,(WORD)tmp1);
/*TODO*///		break;
/*TODO*///
/*TODO*///    case 0x08:  /* DEC ew */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.incdec_r16 : cycles.incdec_m16;
/*TODO*///		tmp = GetRMWord(ModRM);
/*TODO*///		tmp1 = tmp-1;
/*TODO*///
/*TODO*///		SetOFW_Sub(tmp1,1,tmp);
/*TODO*///		SetAF(tmp1,tmp,1);
/*TODO*///		SetSZPF_Word(tmp1);
/*TODO*///
/*TODO*///		PutbackRMWord(ModRM,(WORD)tmp1);
/*TODO*///		break;
/*TODO*///
/*TODO*///    case 0x10:  /* CALL ew */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.call_r16 : cycles.call_m16;
/*TODO*///		tmp = GetRMWord(ModRM);
/*TODO*///		ip = I.pc - I.base[CS];
/*TODO*///		PUSH(ip);
/*TODO*///		I.pc = (I.base[CS] + (WORD)tmp) & AMASK;
/*TODO*///		CHANGE_PC(I.pc);
/*TODO*///		break;
/*TODO*///
/*TODO*///	case 0x18:  /* CALL FAR ea */
/*TODO*///		i86_ICount[0] -= cycles.call_m32;
/*TODO*///		tmp = I.sregs[CS];	/* HJB 12/13/98 need to skip displacements of EA */
/*TODO*///		tmp1 = GetRMWord(ModRM);
/*TODO*///		ip = I.pc - I.base[CS];
/*TODO*///		PUSH(tmp);
/*TODO*///		PUSH(ip);
/*TODO*///#ifdef I286
/*TODO*///		i286_code_descriptor(GetnextRMWord, tmp1);
/*TODO*///#else
/*TODO*///		I.sregs[CS] = GetnextRMWord;
/*TODO*///		I.base[CS] = SegBase(CS);
/*TODO*///		I.pc = (I.base[CS] + tmp1) & AMASK;
/*TODO*///#endif
/*TODO*///		CHANGE_PC(I.pc);
/*TODO*///		break;
/*TODO*///
/*TODO*///    case 0x20:  /* JMP ea */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.jmp_r16 : cycles.jmp_m16;
/*TODO*///		ip = GetRMWord(ModRM);
/*TODO*///		I.pc = (I.base[CS] + ip) & AMASK;
/*TODO*///		CHANGE_PC(I.pc);
/*TODO*///		break;
/*TODO*///
/*TODO*///    case 0x28:  /* JMP FAR ea */
/*TODO*///		i86_ICount[0] -= cycles.jmp_m32;
/*TODO*///		
/*TODO*///#ifdef I286
/*TODO*///		tmp = GetRMWord(ModRM);
/*TODO*///		i286_code_descriptor(GetnextRMWord, tmp);
/*TODO*///#else
/*TODO*///		I.pc = GetRMWord(ModRM);
/*TODO*///		I.sregs[CS] = GetnextRMWord;
/*TODO*///		I.base[CS] = SegBase(CS);
/*TODO*///		I.pc = (I.pc + I.base[CS]) & AMASK;
/*TODO*///#endif
/*TODO*///		CHANGE_PC(I.pc);
/*TODO*///		break;
/*TODO*///
/*TODO*///    case 0x30:  /* PUSH ea */
/*TODO*///		i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.push_r16 : cycles.push_m16;
/*TODO*///		tmp = GetRMWord(ModRM);
/*TODO*///		PUSH(tmp);
/*TODO*///		break;
/*TODO*///	 }
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void PREFIX86(_invalid)(void)
/*TODO*///{
/*TODO*///#ifdef I286
/*TODO*///	i286_trap2(ILLEGAL_INSTRUCTION);
/*TODO*///#else
/*TODO*///	 /* makes the cpu loops forever until user resets it */
/*TODO*///	/*{ extern int debug_key_pressed; debug_key_pressed = 1; } */
/*TODO*///	logerror("illegal instruction %.2x at %.5x\n",PEEKBYTE(I.pc), I.pc);
/*TODO*///	I.pc--;
/*TODO*///	i86_ICount[0] -= 10;
/*TODO*///#endif
/*TODO*///}
/*TODO*///#endif
/*TODO*///    
}
