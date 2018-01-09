import java.awt.*;
import java.applet.*;
import java.util.*;

class Road{
  
  // length of the road
  public final int LENGTH = 300;
  // Maximum speed
  public final int MAXSPEED = 8;

  // point of measurement
  public final int mp =LENGTH-1;
  // length of the measurement
  public final int ml = LENGTH-200;
  // dummy for infinity
  public final double DBL_INF = 99999.99;
  
  // my private colors
  private final Color streetcolor = Color.white;
  private final Color background = Color.white;
  private final Color foreground = Color.black;
  private final Color linecolor = Color.blue;
  
  // colors for different velocities
  private final Color free = Color.white;
  private final Color v0color = Color.red;
  private final Color v1color = new Color(255,51,0);
  private final Color v2color = new Color(255,102,0);
  private final Color v3color = new Color(255,153,0);
  private final Color v4color = new Color(255,204,0);
  private final Color v5color = Color.orange;
  private final Color v6color = new Color(207,255,0);
  private final Color v7color = new Color(153,255,0);
  private final Color v8color = new Color(0,255,0);
  private final Color vcolor[]={v0color,v1color,v2color,v3color,v4color,v5color,v6color,v7color,v8color};
  
  // the road as an array of speeds
  private int[] speed;
  // number of vehicles on the road
  private int cars;
  // 1/number of vehicles
  private double invcars;
  // local density of vehicles on the road [veh/site]
  private double dens;
  // local mean velocity [sites/timestep]
  private double v;
  // local flow of vehicles [vehicles/time]
  private double flow;
  // deceleration probability
  private double p_dec;
  // global frequency of gaps
  private double[] gapfreq;
  // global frequency of velocities
  private double[] vfreq;
  
  ///////////////////////////////////
  // Init the road for the simulation

  public Road(double density){  
    // the road as an array of speeds
    speed = new int[LENGTH];
    // frequency of the gaps
    gapfreq = new double[LENGTH];
    // frequency of the velocities
    vfreq = new double[MAXSPEED+1];
    
    // How many vehicles?
    cars = 0;
    int cars_abs = (int)(density*LENGTH);
    invcars = 1.0/((cars_abs > 0)? cars_abs: 1.0/DBL_INF);
    
    // clear road
    for (int i=0;i<LENGTH;i++)
      speed[i] = -1;
    
    // put vehicles on the road
    while (cars < cars_abs){
      int i = (int)(Math.random()*LENGTH);		
      if (speed[i] == -1){
	speed[i] = 0;
	cars++;
      }
    }
  }

  ////////////////////////////////////
  // update of the cellular automaton
  
  public void update(double p_dec[], double prob_create,int modeltype,double p_dec_incr_sts,double p_dec_incr_tsqr,int maxspeed){  

    // number of vehicles
    int car_local = (int)(prob_create*LENGTH);
    // the different between the actual and the requested number of vehicles
    int diff_cars = cars-car_local;
    
    for(int i=0;i<LENGTH;i++)
      gapfreq[i]=0.0;
    for(int i=0;i<=MAXSPEED;i++)
      vfreq[i]=0.0;
    
    // match the number of vehicles
    if (diff_cars > 0){
      // number of vehivles, that have to be killed
      int kill_cars = 0;
      while (kill_cars < diff_cars){
	int i = (int)(Math.random()*LENGTH);		
	if (speed[i] != -1){
	  speed[i] = -1;
	  kill_cars++;
	}
      }
      cars = car_local;
      invcars  = 1.0/((cars > 0)? cars: 1.0/DBL_INF);
    }
    else
      if (diff_cars < 0){
	// number of vehivles, that have to be created
	int new_cars = 0;
	while (new_cars < Math.abs(diff_cars)){
	  int i = (int)(Math.random()*LENGTH);		
	  if (speed[i] == -1){
	    speed[i] = MAXSPEED;
	    new_cars++;
	  }
	}
	cars = car_local;
	invcars  = 1.0/((cars > 0)? cars: 1.0/DBL_INF);
      }
    
    // where is the 1st vehicle
    int i = -1;
    while((++i < LENGTH) && (speed[i] == -1));

    // go on until reaching the end of the lane
    while (i < LENGTH){
      // searching for the vehicle ahead
      int gap = i;
      while (speed[++gap%LENGTH] == -1);
      car_local++;
      
      // distance between two consecutive vehicles
      gap-=(i+1);
      gapfreq[gap]+=((invcars < DBL_INF)? invcars: 0);
      
      ////////////////////////////////////////////
      // Update rules of the cellular automaton //
      ////////////////////////////////////////////
      
      int speed_old = speed[i];

      // Acceleration
      if (gap > speed_old)
	speed[i] = Math.min(speed_old+1,maxspeed);
      // Slow down to prevent crashes
      else
	speed[i] = gap;

      // Stochastic behavior
      // Slow down with prob p_dec
      // Different model types are applied! See Info...
      // 0=Standard
      // 1=T²
      //       All vehicles with gap<=1 are suffering from a
      //       increased deceleration probability
      // 2=VDR
      //       Comparable to modeltype 0, but with explicit 
      //       p_dec as a function of speed p_dec = p_dec(v)
      // 3=Fukui-Ishibashi
      //       Full acceleration (unlike the Standard-CA with Delta_v=+1
      //       here v <- min(v_max,gap)
      //       p_dec only applied for v=v_max
      switch(modeltype){
      case 0:{
	if ((speed[i] > 0) && (Math.random() <= p_dec[speed_old]))
	  speed[i]--;
	break;
      }
      case 1:{
	if ((speed[i] > 0) && (Math.random() <= p_dec[speed_old]))
	  speed[i]--;
	break;
      }
      case 2:{
	if (gap <= 1){
	  if ((speed[i] > 0) && (Math.random() <= Math.min(p_dec[speed[i]]+p_dec_incr_tsqr,1.0)))
	    speed[i]--;
	}
	else
	  if ((speed[i] > 0) && (Math.random() <= p_dec[speed[i]]))
	    speed[i]--;
	break;
      }
      case 3:{
	if ((speed[i] == maxspeed) && (Math.random() <= p_dec[speed[i]]))
	  speed[i]--;
	break;
      }
      }
      
      vfreq[speed[i]]+=((invcars < DBL_INF)? invcars: 0);
      // next vehicle is on site j
      i+=(gap+1);
    }
    
    // Move the vehicles
    
    // Where is the 1st vehicle?
    car_local = 0;
    i = -1;
    while((++i < LENGTH) && (speed[i] == -1));

    // go on until reaching the end of the lane
    while (i < LENGTH){
      car_local++;
      int inext = i+speed[i];
      int j = inext%LENGTH;
      // exchange the positions
      if (i != j){
	speed[j] = speed[i];
	speed[i] = -1;
      }
      // searching for the vehicle ahead
      while (speed[++inext%LENGTH] == -1);
      i = inext;
    }
  }
  
  //////////////////
  // space-time-plot
  
  public void paint(Graphics g,int row,int dotdist,int dotsize,int xshift){  
    int i;
    for (i = 0; i < LENGTH; i++){
      g.setColor(free);
      if (speed[i] >= 0) g.setColor(vcolor[speed[i]]);      
      g.fillRect((xshift+i)*dotdist,row,dotsize,dotsize);
    }
    g.setColor(background);   
  }
  
  /////////////////////////////////
  // plot the indianapolis-scenario
  
  public void indypaint(Graphics g, int dotdist, int dotsize,int xm,int ym,int radius){  

    // degrees per vehicle
    double f = 2.0*Math.PI/(double)LENGTH;
    for (int i = 0; i < LENGTH; i++){
      // angle of a vehicle
      double rad = f*i;
      // Erase a vehicle
      if (speed[i] == -1){
	g.setColor(streetcolor);
	g.fillRect((int)(xm+Math.cos(rad)*radius), (int)(ym+Math.sin(rad)*radius), dotsize, dotsize);
      }
      // plot a vehicle
      else{
	g.setColor(foreground);
	g.fillRect((int)(xm+Math.cos(rad)*radius), (int)(ym+Math.sin(rad)*radius), dotsize, dotsize);
      }
    }
  }

  ////////////////////
  // local measurement
  
  public void measure(double pdec){
    int vsum=0;
    int rhoc=0;
    
    for(int i=mp;i>mp-ml;i--)
      if (speed[i] >= 0){
	vsum+=speed[i];
	rhoc++;
      }
    v = (double)(vsum)/(double)((rhoc > 0) ? rhoc : 1 );
    dens =(double)(rhoc)/(double)(ml);
    flow = v*dens;
    p_dec = pdec;
  }  
  
  ////////////////
  // plot diagrams
  
  void diagram(Graphics g, int x1, int y1, int intdx, int intdy, double dbldx, double dbldy, int index){
    double xValue;
    double yValue;
    g.setColor(foreground);
    // fundamental diagrams
    if (index < 3){
      switch(index){
      case 0:{
	// local flow = local flow(local density)
	xValue = 0.97; //original    dens
	yValue = flow;
	g.setColor(background);
	g.fillRect(x1-25,y1-(int)(0.65/dbldy*intdy)-12,25,12);
	g.setColor(foreground);
	g.drawString(""+(int)(flow*60),x1-25,y1-(int)(0.65/dbldy*intdy));
	break;
      }
      case 1:{
	/*// local mean velocity = local mean velocity(local density)
	xValue = dens; 
	yValue = v/MAXSPEED;*/
	//  
	xValue = dens;
	yValue = flow;
	break;
      }
      default:{
	// local mean velocity = local mean velocity(local flow)
	xValue = flow;
	yValue = v/MAXSPEED;
	break;
      }
      }
      g.drawRect(x1+(int)(xValue/dbldx*intdx),y1-(int)(yValue/dbldy*intdy),1,1);
    }
    
    // bar charts
    else{
      // bar chart of the frequency of velocities
      if (index == 3){
	// clear diagram
	g.setColor(background);
	g.fillRect(x1+1,y1-intdy,intdx-1,intdy);
	drawArrow(g,x1,y1-intdy,10,8,270,true,foreground);
	drawArrow(g,x1+intdx,y1,10,8,0,true,foreground);
	// bar widht
	int bar = intdx/(MAXSPEED+1);
	g.setColor(foreground);
	for (int i=0;i<=MAXSPEED;i++){
	  g.setColor(vcolor[i]);
	  int y = (int)(vfreq[i]/dbldy*intdy);
	  g.fillRect(x1+i*bar+1,y1-y,bar-1,y);
	}
      }
      // bar chart of the frequency of gaps
      else{
	// clear diagram
	g.setColor(background);
	g.fillRect(x1+1,y1-intdy,intdx-1,intdy);
	drawArrow(g,x1,y1-intdy,10,8,270,true,foreground);
	drawArrow(g,x1+intdx,y1,10,8,0,true,foreground); 
	// number of bars
	int xmax = 18;
	// bar width
	int bar = intdx/xmax;
	g.setColor(foreground);
	for (int i=0;i<xmax;i++){
	  int y = (int)(gapfreq[i]/dbldy*intdy);
	  g.fillRect(x1+i*bar+1,y1-y,bar-1,y);
	}
      }
    }
  }

  ///////////////
  // diagram axes

  public void diagramAxis(Graphics g, int xOr, int yOr, int xDiag, int yDiag){
    g.drawLine(xOr,yOr,xDiag,yDiag);
    drawArrow(g,xDiag,yDiag,10,8,(xOr == xDiag)? 270: 0,true,foreground);
  }
  
  /////////////////////////////////////////////
  // draw the road of the indianapolis-scenario
  
  public void street(Graphics g, int xm, int ym, int r1, int r2){
    g.setColor(streetcolor);
    g.fillOval(xm-r1,ym-r1,2*r1,2*r1);
    g.setColor(background);
    g.fillOval(xm-r2,ym-r2,2*r2,2*r2);
    g.setColor(linecolor);
    g.drawLine(xm+r2-20,ym,xm+r1+20,ym);
    g.setColor(foreground);
    g.drawArc(xm-r2+30,ym-r2+30,2*r2-60,2*r2-60,0,-270);
    drawArrow(g, xm,ym-r2+30,10,8,355, true, Color.black);
  }

  /////////////////
  // clear diagrams
  
  public void ClearDiagrams(Graphics bg,int xOrigin[],int yOrigin[],int xDiagram[],int yDiagram[]){
    bg.setColor(background);
    for (int i=0;i<5;i++){
      bg.fillRect(xOrigin[i]+1,yOrigin[i]-yDiagram[i],xDiagram[i]-1,yDiagram[i]);
      drawArrow(bg,xOrigin[i],yOrigin[i]-yDiagram[i],10,8,270,true,foreground);
      drawArrow(bg,xOrigin[i]+xDiagram[i],yOrigin[i],10,8,0,true,foreground);
    }
    bg.setColor(foreground);
  }

  //////////////////////////////
  // blue line at the first site
  
  public void bluelines(Graphics g,int x1,int y1,int x2,int y2){  
    Color old_color = g.getColor();
    g.setColor(linecolor);
    g.fillRect(x1,y1,x2,y2);
    g.setColor(old_color);
  }
  
  ///////////////////////////////////////////////////////
  // Labels of the space-time-plot
  // The letters of the ordinate are arranged vertically.
  
  public void SpaceTimeLabels(Graphics g,int x1,int y1,int xA1,int yA1,int length1,String label1,int x2,int y2,int xA2,int yA2,int length2,String[] label2){  
    Color color = foreground;
    Color old_color = g.getColor();
    g.setColor(color);
    g.drawString(label1,x1,y1);
    g.drawLine(xA1,yA1,xA1+length1,yA1);
    drawArrow(g,xA1+length1,yA1,10,8,0,true,color);
    for (int i=0;i<4;i++)
      g.drawString(label2[i],x2-5,y2+i*15);
    g.drawLine(xA2,yA2,xA2,yA2+length2);
    drawArrow(g,xA2,yA2+length2,10,8,90,true,color);
    g.setColor(old_color);
  }
  
  //////////////////////////////////////////////////////////
  // plot an arrow with any properties like size, angle, ...
  
  public void drawArrow(Graphics g,int xA,int yA,int Length,int Width,int deg,boolean filled,Color color){
    
    double Angle=(double)deg/180.0*Math.PI;
    double sinA = Math.sin(Angle);
    double cosA = Math.cos(Angle);
    int x1 = xA-(int)(cosA*Length + sinA*Width/2);
    int y1 = yA-(int)(sinA*Length - cosA*Width/2);
    
    Polygon filledPolygon = new Polygon();
    filledPolygon.addPoint(xA, yA);
    filledPolygon.addPoint(x1,y1);
    filledPolygon.addPoint(x1+(int)(sinA*Width),y1-(int)(cosA*Width));
    Color old_color = g.getColor();
    g.setColor(color);
    if(filled)
      g.fillPolygon(filledPolygon); 
    else
      g.drawPolygon(filledPolygon); 
    g.setColor(old_color);
  }
}  // End of "class Road"
