package net.minecraft.minechem;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_Minechem;

import org.lwjgl.opengl.GL11;

public class GuiFission extends GuiMinechemMachine {
	
	public GuiFission(EntityPlayer entityplayer, TileEntity tileentity)
	{
		super(new ContainerFission(entityplayer.inventory, (TileEntityFission)tileentity));
		tileMachine = (TileEntityMinechemMachine)tileentity;
	}
	
	protected void drawGuiContainerForegroundLayer()
    {
        fontRenderer.drawString("Fission Reactor", 46, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
        drawIC2Information();
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int k = mc.renderEngine.getTexture("/minechem/fission.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(k);
        int l = (width - xSize) / 2;
        int i1 = (height - ySize) / 2;
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        
        int j1 = tileMachine.getTimer();
        if(j1 > 0)
        {
        	int j2 = 0;
        	int k1 = (int)(16F * (1.0F - (float)j1 / (float)tileMachine.timerDuration));
        	if(k1 < 7)
        		j2 = 0;
        	else
        		j2 = k1-7;
        	
        	drawTexturedModalRect(l + 80 + k1, i1 + 29, 176+k1, 0, 2, 31);
        }
	}

}
