import java.awt.*;
import java.applet.*;
import java.util.*;

class RoadCanvas extends Canvas{  
  
  // The simulated road
  private Road freeway;

  // Buffer for painting the road
  private Image buffer;
  
  // diagram labels
  //private String SpaceLabel = "space";
  //private String[] TimeLabel = {"t","i","m","e"};
  private String SpaceLabel;
  private String[] TimeLabel;

  private final String x1="Zeit";
  private final String y1="phi";
  private final String x2="rho";
  private final String y2="phi";
  private final String x3="phi";
  private final String y3="v";
  private final String x4="v";
  private final String y4="f";
  private final String x5="gap";
  private final String y5="f";
  
  private final String[] xAxisLabel={x1,x2,x3,x4,x5};
  private final String[] yAxisLabel={y1,y2,y3,y4,y5};
  
  // dot sizes
  private final int DOTSIZE = 1;   
  private final int XDOTDIST = 1;
  
  // counter of the rows in the space-time-plot
  private int row;
  
  // some properties of the several graphic outputs
  private int xsize;
  private int ysize;
  private int xsizeSTD;
  private int ysizeSTD;
  private int xsizeIndy;
  private int ysizeIndy;
  private int xsizeDiagram;
  private int ysizeDiagram;
  private int xsizeDiagramPart;
  private int ysizeDiagramPart;
  
  private final int xShift = 17;
  private final int yShift = 17;
  private final int bluebar = 3;
    
  // some properties of the indianapolis scenario
  private int radIn;
  private int radOut;
  private int radVeh;
  private int xIndyMid;
  private int yIndyMid;
  
  // some properties of the diagrams
  private int[] xDiagram;
  private int[] yDiagram;
  
  private int[] xOrigin;
  private int[] yOrigin;
  
  private double[] xDelta;
  private double[] yDelta;
  
  // Init the class
  RoadCanvas(double density,int language){  
    freeway = new Road(density);
    row = 0;      
    SpaceLabel = new String();
    TimeLabel = new String[4];
    switch(language){
    case 1:
      SpaceLabel = "Ort";
      TimeLabel[0] = "Z";
      TimeLabel[1] = "e";
      TimeLabel[2] = "i";
      TimeLabel[3] = "t";
      break;
    default:
      SpaceLabel = "space";
      TimeLabel[0] = "t";
      TimeLabel[1] = "i";
      TimeLabel[2] = "m";
      TimeLabel[3] = "e";
      break;
    }
  }

  ///////////////////////////////////////////
  // Update routine: simulation and painting

  public void update(double p_dec[], double density, int counter,int modeltype,double p_dec_inc_sts,double p_dec_inc_tsqr,int maxspeed){  
  
    // No buffer available
    if (buffer == null){ 
      xsize = size().width;
      ysize = size().height;
      xsizeSTD = xsize/2;
      ysizeSTD = ysize/2;
      xsizeIndy = xsize/2;
      ysizeIndy = ysize/2;
      xsizeDiagram = xsize/2;
      ysizeDiagram = ysize;
      xsizeDiagramPart = xsizeDiagram/8;
      ysizeDiagramPart = ysize/40;
      
      radIn = (int)(0.36*xsizeIndy); //** size of the street
      radOut = (int)(0.38*xsizeIndy);
      radVeh = (int)(0.37*xsizeIndy);
      
      xIndyMid = xsizeIndy/2+xShift+bluebar; //** midpoint of circle
      yIndyMid = ysizeSTD+ysizeIndy/2+yShift/2;
      
      xDiagram = new int[5];
      yDiagram = new int[5];
      xOrigin = new int[5];
      yOrigin = new int[5];
      xDelta = new double[5];
      yDelta = new double[5];
      
      for (int i=0;i<3;i++){
	xDiagram[i] = xsizeDiagram-2*xsizeDiagramPart;
	yDiagram[i] = (ysizeDiagram-4*ysizeDiagramPart)/4;
	xOrigin[i] = xsizeSTD+xsizeDiagramPart;
	yOrigin[i] = (i+1)*(yDiagram[i]+ysizeDiagramPart);
	xDelta[i] = 1.0;
	yDelta[i] = 1.0;
      }
      
      for (int i=3;i<5;i++){
	xDiagram[i] = (int)(0.44*xDiagram[0]);
	yDiagram[i] = (ysizeDiagram-4*ysizeDiagramPart)/4;
	xOrigin[i] = (i == 3)? xsizeSTD+xsizeDiagramPart: xsizeSTD+xsizeDiagram/2;
	yOrigin[i] = 4*(yDiagram[i]+ysizeDiagramPart)-8;
	xDelta[i] = 1.0;
	yDelta[i] = 1.0;
      }
      
      // draw all diagrams
      buffer = createImage(xsize,ysize);
      Graphics bg = buffer.getGraphics();
      for (int i=0;i<5;i++){
	freeway.diagramAxis(bg,xOrigin[i],yOrigin[i],xOrigin[i]+xDiagram[i],yOrigin[i]);
	bg.drawString(xAxisLabel[i],xOrigin[i]+xDiagram[i]+3,yOrigin[i]+5);
	
	freeway.diagramAxis(bg,xOrigin[i],yOrigin[i],xOrigin[i],yOrigin[i]-yDiagram[i]);
	bg.drawString(yAxisLabel[i],xOrigin[i]-20,yOrigin[i]-yDiagram[i]+25);
      }
      freeway.street(bg,xIndyMid,yIndyMid,radOut,radIn);
      freeway.bluelines(bg,xShift,yShift,bluebar-1,ysizeSTD);
      freeway.SpaceTimeLabels(bg,xsizeSTD/3,3*yShift/4,xsizeSTD/2,yShift/2,50,SpaceLabel,xShift/2,yShift+ysizeSTD/4,xShift/2,yShift+ysizeSTD/2,50,TimeLabel);
    }
    
    // Update routine: simulation
    freeway.update(p_dec,density,modeltype,p_dec_inc_sts,p_dec_inc_tsqr,maxspeed);
    // update all diagrams
    Graphics bg = buffer.getGraphics();
    // update space-time-plot
    freeway.paint(bg,xShift+row,XDOTDIST,DOTSIZE,xShift+bluebar);
    // update indy-scenario
    freeway.indypaint(bg,XDOTDIST*2,DOTSIZE*2,xIndyMid,yIndyMid,radVeh);
    
    // if enabled, plot diagrams after the measurement
    
    if (counter == 0){
      freeway.measure(p_dec[1]);
      for (int i=0;i<5;i++)
	freeway.diagram(bg,xOrigin[i],yOrigin[i],xDiagram[i],yDiagram[i],xDelta[i],yDelta[i],i);
    bg.copyArea(xOrigin[0]+3,yShift,xDiagram[0],yDiagram[0]-5,-DOTSIZE,0);
    }
    
    
    // move space-time-plot upwards
    if (row < ysizeSTD-DOTSIZE)
      row+=DOTSIZE;
    else
      bg.copyArea(xShift+bluebar,DOTSIZE+yShift,xsizeSTD-xShift+bluebar,ysizeSTD-DOTSIZE,0,-DOTSIZE);
    
      
    bg.dispose();
    repaint();
  }
  
  //////////////////////////////////
  // Cleaning the diagrams
  
  public void ClearDiagrams(){
    Graphics bg = buffer.getGraphics();
    freeway.ClearDiagrams(bg,xOrigin,yOrigin,xDiagram,yDiagram);
    bg.dispose();
    repaint();
  }
  
  //////////////////////////////////
  // Draing the picture
  
  public void paint(Graphics g){  
    if (buffer != null) 
      g.drawImage(buffer, 0, 0, null);  
  }
  
  //////////////////////////////////
  // Update the picture
  
  public void update(Graphics g){  
    paint(g);
  }
  
}  // End of "class RoadCanvas extends Canvas"

