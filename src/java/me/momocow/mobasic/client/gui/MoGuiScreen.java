package me.momocow.mobasic.client.gui;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public abstract class MoGuiScreen extends GuiScreen
{
	private Map<Integer, List<String>> tooltips= new HashMap<Integer, List<String>>();
	
	protected String unlocalizedName = "";
	public int rowHeight = 10;
	public int colWidth = 10;
	public int guiHeight = 0;
	public int guiWidth = 0;
	public int offsetX = 0;
	public int offsetY = 0;
	
	public MoGuiScreen(int w, int h, int ox, int oy)
	{
		this.setGuiSize(w, h);
		this.offsetX = ox;
		this.offsetY = oy;
	}
	
	public MoGuiScreen(int w, int h)
	{
		this(w, h, 0, 0);
	}
	
	/**
	 * Gui initial size 100x100
	 */
	public MoGuiScreen(){
		this(100, 100);
	}
	
	public void setOffset(int ox, int oy)
	{
		this.offsetX = ox;
		this.offsetY = oy;
	}
	
	public void setCenter(int cx, int cy)
	{
		this.offsetX = cx - this.guiWidth / 2;
		this.offsetY = cy - this.guiHeight / 2;
	}
	
	public void setUnlocalizedName(String n)
	{
		unlocalizedName = "gui." + n;
	}
	
	public String getUnlocalizedName()
	{
		return unlocalizedName;
	}
	
	public int getWindowWidth()
	{
		return this.width;
	}
	
	public int getWindowHeight()
	{
		return this.height;
	}
	
	public int getGuiWidth()
	{
		return this.guiWidth;
	}
	
	public int getGuiHeight()
	{
		return this.guiHeight;
	}
	
	public void setGuiSize(int gw, int gh)
	{
		this.guiWidth = gw;
		this.guiHeight = gh;
	}
	
	public void setGuiSize(double gw, double gh)
	{
		this.guiWidth = (int) gw;
		this.guiHeight = (int) gh;
	}
	
	public int getGlobalX(int guiX)
	{
		return this.offsetX + guiX;
	}
	
	public int getGlobalY(int guiY)
	{
		return this.offsetY + guiY;
	}
	
	public int getLocalX(int windowX)
	{
		return windowX - this.offsetX;
	}
	
	public int getLocalY(int windowY)
	{
		return windowY - this.offsetY;
	}
	
	public int getCenterX()
	{
		return this.offsetX + this.getGuiWidth() / 2;
	}
	
	public int getCenterY()
	{
		return this.offsetY + this.getGuiHeight() / 2;
	}
	
	/**
	 * get the Y coord of the nth row
	 * @param rowIdx
	 * @return
	 */
	public int row(int rowIdx)
	{
		return this.getGlobalY(rowIdx * this.rowHeight);
	}
	
	/**
	 * get the X coord of the nth column
	 * @param rowIdx
	 * @return
	 */
	public int col(int colIdx)
	{
		return this.getGlobalX(colIdx * this.colWidth);
	}
	
	@Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException 
	{
		if(keyCode == 18 || keyCode == 1)	//press 'e' or 'esc' to exit
    	{
    		this.changeGui(null);
    	}
	}
	
	public void drawButtonList(int mouseX, int mouseY)
	{
		for (int i = 0; i < this.buttonList.size(); ++i)
        {
            ((GuiButton)this.buttonList.get(i)).drawButton(this.mc, mouseX, mouseY);
        }
	}
	
	protected void openWebLink(URI url)
    {
        try
        {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {url});
        }
        catch (Throwable throwable1)
        {
            Throwable throwable = throwable1.getCause();
            LogManager.getLogger(this.getClass().getName()).error("Couldn\'t open link: {}", new Object[] {throwable == null ? "<UNKNOWN>" : throwable.getMessage()});
        }
    }
	
	public void clearTooltip(int id)
	{
		if(this.tooltips.get(id) != null)
		{
			this.tooltips.get(id).clear();
		}
	}
	
	public void addTooltip(int id, List<String> ttp)
	{
		if(this.tooltips.get(id) != null)
		{
			this.tooltips.get(id).addAll(ttp);
		}
		else
		{
			this.tooltips.put(id, ttp);
		}
	}
	
	public void addTooltip(int id, String ttp)
	{
		if(this.tooltips.get(id) != null)
		{
			this.tooltips.get(id).add(ttp);
		}
		else
		{
			List<String> l = new ArrayList<String>();
			l.add(ttp);
			this.tooltips.put(id, l);
		}
	}
	
	public void drawTooltip(int id, int x, int y)
	{
		if(this.tooltips.get(id) != null)
		{
			this.drawHoveringText(this.tooltips.get(id), x, y);
		}
	}
	
	public void drawCenteredString(FontRenderer fontObj, String text, int centeredX, int y, int colorCode, boolean hasShadow)
	{
		if(hasShadow)
		{
			this.drawCenteredString(fontObj, text, centeredX, y, colorCode);
		}
		else
		{
			fontObj.drawString(text, centeredX - fontObj.getStringWidth(text) / 2, y, colorCode);
		}
	}
	
	/**
	 * <p>Compatible texture drawing method for any size of images</p>
	 * <p>Parameters, x and y, define the right top point on the screen.</p>
	 * <p>Parameters, textureX and textureY, define the right top point on the image.</p>
	 * <p>Parameters, width and height, define an area on the image that you want to draw onto the screen.</p>
	 * <p>Parameters, imageWidth and imageHeight, are the size of the whole image, not the area you want to draw.</p>
	 * <p>You should use the parameter, scale, to change the size of the texture shown on the screen.</p>
	 * @param texture
	 * @param x
	 * @param y
	 * @param textureX
	 * @param textureY
	 * @param width
	 * @param height
	 * @param imageWidth
	 * @param imageHeight
	 * @param scale
	 */
	public static void drawTexturedRect(ResourceLocation texture, double x, double y, double zLevel, int textureX, int textureY, int width, int height, int imageWidth, int imageHeight, double scaleWidth, double scaleHeight) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        double minU = (double)textureX / (double)imageWidth;
        double maxU = (double)(textureX + width) / (double)imageWidth;
        double minV = (double)textureY / (double)imageHeight;
        double maxV = (double)(textureY + height) / (double)imageHeight;
        
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(x + scaleWidth*(double)width, y + scaleHeight*(double)height, (double)zLevel).tex(maxU, maxV).endVertex();
        vertexbuffer.pos(x + scaleWidth*(double)width, y, zLevel).tex(maxU, minV).endVertex();
        vertexbuffer.pos(x, y, (double)zLevel).tex(minU, minV).endVertex();
        vertexbuffer.pos(x, y + scaleHeight*(double)height, zLevel).tex(minU, maxV).endVertex();
        tessellator.draw();
    }
	
	/**
	 * Casting the data type of parameters for the method {@linkplain #drawTexturedRect(ResourceLocation, double, double, int, int, int, int, int, int, double) drawTexturedRect}
	 */
	public static void drawTexturedRect(ResourceLocation texture, int x, int y, float zLevel, int textureX, int textureY, int width, int height, int imageWidth, int imageHeight, double scaleWidth, double scaleHeight)
	{
		MoGuiScreen.drawTexturedRect(texture, (double)x, (double)y, (double)zLevel, textureX, textureY, width, height, imageWidth, imageHeight, scaleWidth, scaleHeight);
	}
	
	/**
	 * Auto-scale version of the method {@linkplain #drawTexturedRect(ResourceLocation, double, double, int, int, int, int, int, int, double) drawTexturedRect}
	 * It will automatically scale to fit the expected size of Gui
	 * @param texture
	 * @param x
	 * @param y
	 * @param zLevel
	 * @param textureX
	 * @param textureY
	 * @param width
	 * @param height
	 * @param imageWidth
	 * @param imageHeight
	 * @param guiWidth expected width of the gui
	 * @param guiHeight expected height of the gui
	 */
	public static void drawProportionTexturedRect(ResourceLocation texture, int x, int y, float zLevel, int textureX, int textureY, int width, int height, int imageWidth, int imageHeight, int guiWidth, int guiHeight)
	{
		double scale = Math.min((double)guiHeight / (double) height, (double)guiWidth / (double)width);
		MoGuiScreen.drawTexturedRect(texture, (double)x, (double)y, (double)zLevel, textureX, textureY, width, height, imageWidth, imageHeight, scale, scale);
	}
	
	/**
	 * Auto-scale version of the method {@linkplain #drawTexturedRect(ResourceLocation, double, double, int, int, int, int, int, int, double) drawTexturedRect}
	 * It will automatically scale to fit the expected size of Gui
	 * @param texture
	 * @param x
	 * @param y
	 * @param textureX
	 * @param textureY
	 * @param width
	 * @param height
	 * @param imageWidth
	 * @param imageHeight
	 * @param guiWidth expected width of the gui
	 * @param guiHeight expected height of the gui
	 */
	public static void drawPartialScaleTexturedRect(ResourceLocation texture, int x, int y, float zLevel, int textureX, int textureY, int width, int height, int imageWidth, int imageHeight, int guiWidth, int guiHeight)
	{
		double scaleWidth = (double)guiWidth / (double)width;
		double scaleHeight = (double)guiHeight / (double) height;
		MoGuiScreen.drawTexturedRect(texture, (double)x, (double)y, (double)zLevel, textureX, textureY, width, height, imageWidth, imageHeight, scaleWidth, scaleHeight);
	}
	
	@Override
    public void handleMouseInput() throws IOException {
    	super.handleMouseInput();
    	
    	int wheelMove = Mouse.getEventDWheel();
    	if(wheelMove != 0)
    	{
    		this.mouseWheelMove(wheelMove);
    	}
    }

	public void mouseWheelMove(int wheelMove) {}

	public void changeGui(GuiScreen screen)
    {
    	this.mc.displayGuiScreen((GuiScreen)screen);

        if (this.mc.currentScreen == null)
        {
            this.mc.setIngameFocus();
        }
    }
}
