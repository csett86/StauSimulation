import java.awt.*;
import java.applet.*;
import java.util.*;

/////////////////////////////////////////////////
// The message boxes for changing the p_dec's

class MessageBox extends Frame{

  // used language
  public int local_language;

  // Message box alive?
  public boolean isRunning;

  // Close button
  private Button close;

  // The scrollbars
  private Scrollbar p_dec_s;
  private Scrollbar p_dec_s_inc;
  private Scrollbar p_dec_s_0;
  private Scrollbar p_dec_s_1;
  private Scrollbar p_dec_s_2;
  private Scrollbar p_dec_s_3;
  private Scrollbar p_dec_s_4;
  private Scrollbar p_dec_s_5;
  private Scrollbar p_dec_s_6;
  private Scrollbar p_dec_s_7;
  private Scrollbar p_dec_s_8;
  private Scrollbar p_dec_s_9;

  // The labels
  private Label p_dec_s_name;
  private Label p_dec_s_inc_name;
  private Label p_dec_s_0_name;
  private Label p_dec_s_1_name;
  private Label p_dec_s_2_name;
  private Label p_dec_s_3_name;
  private Label p_dec_s_4_name;
  private Label p_dec_s_5_name;
  private Label p_dec_s_6_name;
  private Label p_dec_s_7_name;
  private Label p_dec_s_8_name;
  private Label p_dec_s_9_name;

  // ... and more labels
  private Label p_dec_l;
  private Label p_dec_l_inc;  
  private Label p_dec_l_0;
  private Label p_dec_l_1;
  private Label p_dec_l_2;
  private Label p_dec_l_3;
  private Label p_dec_l_4;
  private Label p_dec_l_5;
  private Label p_dec_l_6;
  private Label p_dec_l_7;
  private Label p_dec_l_8;
  private Label p_dec_l_9;

  // For T≤ the resulting deceleration prob.
  private Label p_dec_result_name;
  private Label p_dec_result_value;

  // A textfield
  private TextArea text;

  // Some nice panels
  private Panel panel_text;
  private Panel panel_scroll;
  private Panel panel_button;

  // Local variables
  // speed, deceleration prob's ...
  private int local_maxspeed;
  private double[] local_p_dec_array;
  private double local_p_dec;
  private int local_p_dec_ini;
  private double local_p_dec_inc;  
  private int local_p_dec_inc_ini;

  // Which model type we use?
  private int modeltype;

  /////////////////
  // Standard-model

  public MessageBox(double p_dec,int language){

    local_language = language;
    isRunning = false;
    modeltype = 0;
    local_p_dec = p_dec;
    local_p_dec_ini = (int)(local_p_dec*100);
    
    switch(local_language){
    case 1:
      close = new Button("Schlieﬂen");
      text = new TextArea("Standard-CA-Modell",20,50);
      p_dec_s_name = new Label("Wahrsch. P");
      break;
      // Default language is English
    default:
      close = new Button("Close");
      text = new TextArea("Standard-CA-model",20,50);
      p_dec_s_name = new Label("Prob. P");
      break;
    }
    panel_text = new Panel();
    panel_scroll = new Panel();
    panel_button = new Panel();

    panel_text.setLayout(new GridLayout(1,1));
    panel_scroll.setLayout(new GridLayout(2,3));
    panel_button.setLayout(new GridLayout(3,7));

    panel_text.add(text);

    p_dec_s_name.setAlignment(Label.RIGHT);
    p_dec_s = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
    p_dec_l = new Label((new Integer(local_p_dec_ini).toString())+" %");
    for (int i=0;i<3;i++)
      panel_scroll.add(new Label("    "));
    panel_scroll.add(p_dec_s_name);
    panel_scroll.add(p_dec_s);
    panel_scroll.add(p_dec_l);

    for (int i=0;i<12;i++)
      panel_button.add(new Label("   "));
    panel_button.add(close);
    for (int i=0;i<8;i++)
      panel_button.add(new Label("   "));

    add("North",panel_text);
    add("Center",panel_scroll);
    add("South",panel_button);
  }

  /////////////////
  // Takayasu≤-model
  public MessageBox(double p_dec,double p_dec_inc,int language){

    local_language = language;
    isRunning = false;
    modeltype = 1;

    local_p_dec = p_dec;
    local_p_dec_ini = (int)(local_p_dec*100);
    local_p_dec_inc = p_dec_inc;
    local_p_dec_inc_ini = (int)(local_p_dec_inc*100);
    
    switch (local_language){
    case 1:
      close = new Button("Schlieﬂen");
      text = new TextArea("Takayasu≤-Modell",20,50);
      break;
      // Default language is English
    default:
      close = new Button("Close");
      text = new TextArea("Takayasu≤-model",20,50);
      break;
    }
    panel_text = new Panel();
    panel_scroll = new Panel();
    panel_button = new Panel();

    panel_text.setLayout(new GridLayout(1,1));
    panel_scroll.setLayout(new GridLayout(4,3));
    panel_button.setLayout(new GridLayout(3,7));

    panel_text.add(text);

    p_dec_s_name = new Label("    P(g>0)");
    p_dec_s_name.setAlignment(Label.RIGHT);
    p_dec_s = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
    p_dec_l = new Label((new Integer(local_p_dec_ini).toString())+" %");
    p_dec_s_inc_name = new Label("   +P_add(g<=1)");
    p_dec_s_inc_name.setAlignment(Label.RIGHT);
    p_dec_s_inc = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_inc_ini,1,0,100);
    p_dec_l_inc = new Label((new Integer(local_p_dec_inc_ini).toString())+" %");
    p_dec_result_name = new Label(" -> P(g<=1)");
    p_dec_result_name.setAlignment(Label.RIGHT);
    p_dec_result_value = new Label((new Integer(Math.min(local_p_dec_ini+local_p_dec_inc_ini,100)).toString())+" %");
    p_dec_result_value.setAlignment(Label.CENTER);

    for (int i=0;i<3;i++)
      panel_scroll.add(new Label("    "));
    panel_scroll.add(p_dec_s_name);
    panel_scroll.add(p_dec_s);
    panel_scroll.add(p_dec_l);
    panel_scroll.add(p_dec_s_inc_name);
    panel_scroll.add(p_dec_s_inc);
    panel_scroll.add(p_dec_l_inc);
    panel_scroll.add(p_dec_result_name);
    panel_scroll.add(p_dec_result_value);
    panel_scroll.add(new Label("    "));
    
    for (int i=0;i<12;i++)
      panel_button.add(new Label("   "));
    panel_button.add(close);
    for (int i=0;i<8;i++)
      panel_button.add(new Label("   "));

    add("North",panel_text);
    add("Center",panel_scroll);
    add("South",panel_button);
  }
  
  /////////////////
  // VDR-model

  public MessageBox(double p_dec[],int maxspeed,int maxspeed_array,int language){
    
    local_p_dec_array = new double[maxspeed_array];
    local_language = language;
    isRunning = false;
    modeltype = 2;

    String s = new String();

    local_maxspeed = maxspeed;
    for (int v=0;v<maxspeed_array;v++)
      local_p_dec_array[v] = p_dec[v];

    switch (local_language){
    case 1:
      close = new Button("Schlieﬂen");
      text = new TextArea("VDR-Modell",20,50);
      break;
      // Default language is English
    default:
      close = new Button("Close");
      text = new TextArea("VDR-model",20,50);
      break;
    }

    panel_text = new Panel();
    panel_scroll = new Panel();
    panel_button = new Panel();

    panel_text.setLayout(new GridLayout(1,1));
    panel_scroll.setLayout(new GridLayout(maxspeed_array,3));
    panel_button.setLayout(new GridLayout(3,7));

    panel_text.add(text);

    for (int v=0;v<maxspeed_array;v++){
      local_p_dec_ini = (int)(100*p_dec[v]);
      s = "       P(v="+v+")";
      switch(v){
      case 0:{
	p_dec_s_0_name = new Label(s);
	p_dec_s_0 = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
	p_dec_l_0 = new Label((new Integer(local_p_dec_ini).toString())+" %");
	panel_scroll.add(p_dec_s_0_name);
	p_dec_s_0_name.setAlignment(Label.RIGHT);
	panel_scroll.add(p_dec_s_0);
	panel_scroll.add(p_dec_l_0);
	break;
      }
      case 1:{
	p_dec_s_1_name = new Label(s);
	p_dec_s_1 = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
	p_dec_l_1 = new Label((new Integer(local_p_dec_ini).toString())+" %");
	panel_scroll.add(p_dec_s_1_name);
	p_dec_s_1_name.setAlignment(Label.RIGHT);
	panel_scroll.add(p_dec_s_1);
	panel_scroll.add(p_dec_l_1);
	break;
      }
      case 2:{
	p_dec_s_2_name = new Label(s);
	p_dec_s_2 = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
	p_dec_l_2 = new Label((new Integer(local_p_dec_ini).toString())+" %");
	panel_scroll.add(p_dec_s_2_name);
	p_dec_s_2_name.setAlignment(Label.RIGHT);
	panel_scroll.add(p_dec_s_2);
	panel_scroll.add(p_dec_l_2);
	break;
      }
      case 3:{
	p_dec_s_3_name = new Label(s);
	p_dec_s_3 = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
	p_dec_l_3 = new Label((new Integer(local_p_dec_ini).toString())+" %");
	panel_scroll.add(p_dec_s_3_name);
	p_dec_s_3_name.setAlignment(Label.RIGHT);
	panel_scroll.add(p_dec_s_3);
	panel_scroll.add(p_dec_l_3);
	break;
      }
      case 4:{
	p_dec_s_4_name = new Label(s);
	p_dec_s_4 = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
	p_dec_l_4 = new Label((new Integer(local_p_dec_ini).toString())+" %");
	panel_scroll.add(p_dec_s_4_name);
	p_dec_s_4_name.setAlignment(Label.RIGHT);
	panel_scroll.add(p_dec_s_4);
	panel_scroll.add(p_dec_l_4);
	break;
      }
      case 5:{
	p_dec_s_5_name = new Label(s);
	p_dec_s_5 = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
	p_dec_l_5 = new Label((new Integer(local_p_dec_ini).toString())+" %");
	panel_scroll.add(p_dec_s_5_name);
	p_dec_s_5_name.setAlignment(Label.RIGHT);
	panel_scroll.add(p_dec_s_5);
	panel_scroll.add(p_dec_l_5);
	break;
      }
      case 6:{
	p_dec_s_6_name = new Label(s);
	p_dec_s_6 = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
	p_dec_l_6 = new Label((new Integer(local_p_dec_ini).toString())+" %");
	panel_scroll.add(p_dec_s_6_name);
	p_dec_s_6_name.setAlignment(Label.RIGHT);
	panel_scroll.add(p_dec_s_6);
	panel_scroll.add(p_dec_l_6);
	break;
      }
      case 7:{
	p_dec_s_7_name = new Label(s);
	p_dec_s_7 = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
	p_dec_l_7 = new Label((new Integer(local_p_dec_ini).toString())+" %");
	panel_scroll.add(p_dec_s_7_name);
	p_dec_s_7_name.setAlignment(Label.RIGHT);
	panel_scroll.add(p_dec_s_7);
	panel_scroll.add(p_dec_l_7);
	break;
      }
      case 8:{
	p_dec_s_8_name = new Label(s);
	p_dec_s_8 = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
	p_dec_l_8 = new Label((new Integer(local_p_dec_ini).toString())+" %");
	panel_scroll.add(p_dec_s_8_name);
	p_dec_s_8_name.setAlignment(Label.RIGHT);
	panel_scroll.add(p_dec_s_8);
	panel_scroll.add(p_dec_l_8);
	break;
      }
      case 9:{
	p_dec_s_9_name = new Label(s);
	p_dec_s_9 = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
	p_dec_l_9 = new Label((new Integer(local_p_dec_ini).toString())+" %");
	panel_scroll.add(p_dec_s_9_name);
	p_dec_s_9_name.setAlignment(Label.RIGHT);
	panel_scroll.add(p_dec_s_9);
	panel_scroll.add(p_dec_l_9);
	break;
      }
      }
    }
    
    for (int i=0;i<12;i++)
      panel_button.add(new Label("   "));
    panel_button.add(close);
    for (int i=0;i<8;i++)
      panel_button.add(new Label("   "));

    add("North",panel_text);
    add("Center",panel_scroll);
    add("South",panel_button);
  }

  /////////////////
  // Fukui-Ishibashi

  public MessageBox(double p_dec,int maxspeed,int maxspeed_array,int language){

    local_language = language;
    isRunning = false;
    modeltype = 3;
    local_p_dec = p_dec;
    local_p_dec_ini = (int)(local_p_dec*100);
    local_maxspeed = maxspeed;

    switch (local_language){
    case 1:
      close = new Button("Schlieﬂen");
      text = new TextArea("Fukui-Ishibashi-Modell",20,50);
      break;
      // Default language is English
    default:
      close = new Button("Close");
      text = new TextArea("Fukui-Ishibashi-model",20,50);
      break;
    }

    panel_text = new Panel();
    panel_scroll = new Panel();
    panel_button = new Panel();

    panel_text.setLayout(new GridLayout(1,1));
    panel_scroll.setLayout(new GridLayout(2,3));
    panel_button.setLayout(new GridLayout(3,7));

    panel_text.add(text);

    p_dec_s_name = new Label("P(v_max)");
    p_dec_s_name.setAlignment(Label.RIGHT);
    p_dec_s = new Scrollbar(Scrollbar.HORIZONTAL,local_p_dec_ini,1,0,100);
    p_dec_l = new Label((new Integer(local_p_dec_ini).toString())+" %");
    for (int i=0;i<3;i++)
      panel_scroll.add(new Label("    "));
    panel_scroll.add(p_dec_s_name);
    panel_scroll.add(p_dec_s);
    panel_scroll.add(p_dec_l);

    for (int i=0;i<12;i++)
      panel_button.add(new Label("   "));
    panel_button.add(close);
    for (int i=0;i<8;i++)
      panel_button.add(new Label("   "));

    add("North",panel_text);
    add("Center",panel_scroll);
    add("South",panel_button);
  }

  // Close-Button

  public boolean action(Event evt, Object arg){
    switch(local_language){
    case 1:
      if(arg.equals("Schlieﬂen")){
        isRunning = false;
        hide();
        return true;
      }
      break;
    default:
      if(arg.equals("Close")){
        isRunning = false;
        hide();
        return true;
      }
      break;
    }
    return false;
  }
  
  // Handle Window Events

  public boolean handleEvent(Event e){

    switch(e.id){
    case Event.SCROLL_LINE_UP:
    case Event.SCROLL_LINE_DOWN:
    case Event.SCROLL_PAGE_UP:
    case Event.SCROLL_PAGE_DOWN:
    case Event.SCROLL_ABSOLUTE:{
      switch(modeltype){
      case 0:{
	local_p_dec_ini = p_dec_s.getValue();
	local_p_dec = (double)local_p_dec_ini/100;
	p_dec_l.setText((new Integer(local_p_dec_ini).toString())+" %");
	break;
      }
      case 1:{
	local_p_dec_ini = p_dec_s.getValue();
	local_p_dec = (double)local_p_dec_ini/100;
	p_dec_l.setText((new Integer(local_p_dec_ini).toString())+" %");
	local_p_dec_inc_ini = p_dec_s_inc.getValue();
	local_p_dec_inc = (double)local_p_dec_inc_ini/100;
	p_dec_l_inc.setText((new Integer(local_p_dec_inc_ini).toString())+" %");
	p_dec_result_value.setText(new Integer(Math.min(local_p_dec_ini+local_p_dec_inc_ini,100)).toString()+" %");
	break;
      }
      case 2:{
	p_dec_l_0.setText((new Integer(p_dec_s_0.getValue()).toString())+" %");
	local_p_dec_array[0] = (double)p_dec_s_0.getValue()/100;
	p_dec_l_1.setText((new Integer(p_dec_s_1.getValue()).toString())+" %");
	local_p_dec_array[1] = (double)p_dec_s_1.getValue()/100;
	p_dec_l_2.setText((new Integer(p_dec_s_2.getValue()).toString())+" %");
	local_p_dec_array[2] = (double)p_dec_s_2.getValue()/100;
	p_dec_l_3.setText((new Integer(p_dec_s_3.getValue()).toString())+" %");
	local_p_dec_array[3] = (double)p_dec_s_3.getValue()/100;
	p_dec_l_4.setText((new Integer(p_dec_s_4.getValue()).toString())+" %");
	local_p_dec_array[4] = (double)p_dec_s_4.getValue()/100;
	p_dec_l_5.setText((new Integer(p_dec_s_5.getValue()).toString())+" %");
	local_p_dec_array[5] = (double)p_dec_s_5.getValue()/100;
	p_dec_l_6.setText((new Integer(p_dec_s_6.getValue()).toString())+" %");
	local_p_dec_array[6] = (double)p_dec_s_6.getValue()/100;
	p_dec_l_7.setText((new Integer(p_dec_s_7.getValue()).toString())+" %");
	local_p_dec_array[7] = (double)p_dec_s_7.getValue()/100;
	p_dec_l_8.setText((new Integer(p_dec_s_8.getValue()).toString())+" %");
	local_p_dec_array[8] = (double)p_dec_s_8.getValue()/100;
	//p_dec_l_9.setText((new Integer(p_dec_s_9.getValue()).toString())+" %");
	//local_p_dec_array[9] = (double)p_dec_s_9.getValue()/100;
	break;
      }
      case 3:{
	local_p_dec_ini = p_dec_s.getValue();
	local_p_dec = (double)local_p_dec_ini/100;
	p_dec_l.setText((new Integer(local_p_dec_ini).toString())+" %");
	break;
      }
      }
    }
    }

    if ((e.id == Event.WINDOW_DESTROY) || (e.id == Event.KEY_PRESS && e.key==27)){
      isRunning = false;
      if (modeltype == 3){
	p_dec_s_0.enable(true);
	p_dec_s_1.enable(true);
	p_dec_s_2.enable(true);
	p_dec_s_3.enable(true);
	p_dec_s_4.enable(true);
	p_dec_s_5.enable(true);
	p_dec_s_6.enable(true);
	p_dec_s_7.enable(true);
	p_dec_s_8.enable(true);
      }	  
      hide();
      return true;
    }

    return super.handleEvent(e);
  }

  /////////////////
  // Standard-model
  public void show(double p_dec,int language){

    modeltype = 0;
    isRunning = true;
    
    switch(local_language){
    case 1:
      text.setText("          Standard-CA-Modell\n          Regeln fuer paralleles Update\n          =============================\n\n- Fahrzeugposition p\n- Geschw. v und max. Geschw. v_max\n- Luecke g = Zahl der leeren Zellen zum Vordermann\n\n1. Beschleunigung\n       v <- MIN(v+1,v_max,g)\n2. Troedeln\n       mit Wahrsch. P: v <- MAX(v-1,0)\n3. Bewegung\n       p <- p+v");
      super.setTitle("Infos & Details");
      break;
    default:
      text.setText("          Standard-CA-model\n          Rules for the parallel update\n          ======================\n\n- Vehicle position p\n- Velocity v and Maximum Velocity v_max\n- Gap g = Amount of empty cells ahead\n\n1. Acceleration with respect to g\n     v <- MIN(v+1,v_max,g)\n2. Randomization\n     with prob. P do v <- MAX(v-1,0)\n3. Movement\n     p <- p+v");
      super.setTitle("Info & Details");
      break;
    }

    local_p_dec = p_dec;
    local_p_dec_ini = (int)(100*p_dec);

    p_dec_s.setValue(local_p_dec_ini);
    p_dec_l.setText((new Integer(local_p_dec_ini).toString())+" %");

    super.pack();    
    super.show();
    close.requestFocus();
  }

  /////////////////
  // Takayasu≤-model
  public void show(double p_dec,double p_dec_inc,int language){

    modeltype = 1;
    isRunning = true;

    switch(local_language){
    case 1:
      text.setText("          Takayasu≤-Modell\n          Regeln fuer paralleles Update\n          =============================\n\n- Fahrzeugposition p\n- Geschw. v und max. Geschw. v_max\n- Luecke g = Zahl der leeren Zellen zum Vordermann\n\n1. Beschleunigung\n       v <- MIN(v+1,v_max,g)\n2. Troedeln\n       mit Wahrsch. P(g): v <- MAX(v-1,0)\n       Ueblicherweise P(g<=1) > P(g>1)\n3. Bewegung\n       p <- p+v");
      super.setTitle("Infos & Details");
      break;
    default:
      text.setText("          Takayasu≤-model\n          Rules for the parallel update\n          ======================\n\n- Vehicle position p\n- Velocity v and Maximum Velocity v_max\n- Gap g = Amount of empty cells ahead\n\n1. Acceleration with respect to g\n     v <- MIN(v+1,v_max,g)\n2. Randomization\n     with prob. P(g) do v <- MAX(v-1,0)\n       Usually P(g<=1) > P(g>1)\n3. Movement\n     p <- p+v");
      super.setTitle("Info & Details");
      break;
    }

    local_p_dec = p_dec;
    local_p_dec_ini = (int)(100*p_dec);
    local_p_dec_inc = p_dec_inc;
    local_p_dec_inc_ini = (int)(100*local_p_dec_inc);

    p_dec_s.setValue(local_p_dec_ini);
    p_dec_l.setText((new Integer(local_p_dec_ini).toString())+" %");
    p_dec_s_inc.setValue(local_p_dec_inc_ini);
    p_dec_l_inc.setText((new Integer(local_p_dec_inc_ini).toString())+" %");
    p_dec_result_value.setText((new Integer(Math.min(local_p_dec_ini+local_p_dec_inc_ini,100)).toString())+" %");

    super.pack();    
    super.show();
    close.requestFocus();
  }

  public double getlocal_p_dec()
    {
      return local_p_dec;
    }

  public double getlocal_p_dec_inc()
    {
      return local_p_dec_inc;
    }

  public double[] getlocal_p_dec_array()
    {
      return local_p_dec_array;
    }

  /////////////////
  // VDR-model
  public void show(double p_dec[],int maxspeed,int maxspeed_array,int language){

    modeltype = 2;
    isRunning = true;
    
    switch(local_language){
    case 1:
      text.setText("          VDR-Modell\n          Regeln fuer paralleles Update\n          =============================\n\n- Fahrzeugposition p\n- Geschw. v und max. Geschw. v_max\n- Luecke g = Zahl der leeren Zellen zum Vordermann\n\n1. Beschleunigung\n       v <- MIN(v+1,v_max,g)\n2. Troedeln\n       mit Wahrsch. P(v): v <- MAX(v-1,0)\n       (VDR=geschw.-abhaeng. Regeln)\n3. Bewegung\n       p <- p+v");      super.setTitle("Infos & Details");
      break;
    default:
      text.setText("          VDR-model\n          Rules for the parallel update\n          ======================\n\n- Vehicle position p\n- Velocity v and Maximum Velocity v_max\n- Gap g = Amount of empty cells ahead\n\n1. Acceleration with respect to g\n     v <- MIN(v+1,v_max,g)\n2. Randomization\n     with prob. P(v) do v <- MAX(v-1,0)\n       (VDR=velocity-depending rules)\n3. Movement\n     p <- p+v");
      super.setTitle("Info & Details");
      break;
    }

    local_maxspeed = maxspeed;
    for (int v=0;v<maxspeed_array;v++){
      local_p_dec_array[v] = p_dec[v];
      local_p_dec_ini = (int)(100*local_p_dec_array[v]);
      switch(v){
      case 0:{
	p_dec_s_0.setValue(local_p_dec_ini);
	p_dec_l_0.setText((new Integer(local_p_dec_ini).toString())+" %");
	if (0 > maxspeed)
	  p_dec_s_0.enable(false);
	break;
      }
      case 1:{
	p_dec_s_1.setValue(local_p_dec_ini);
	p_dec_l_1.setText((new Integer(local_p_dec_ini).toString())+" %");
	if (1 > maxspeed)
	  p_dec_s_1.enable(false);
	break;
      }
      case 2:{
	p_dec_s_2.setValue(local_p_dec_ini);
	p_dec_l_2.setText((new Integer(local_p_dec_ini).toString())+" %");
	if (2 > maxspeed)
	  p_dec_s_2.enable(false);
	break;
      }
      case 3:{
	p_dec_s_3.setValue(local_p_dec_ini);
	p_dec_l_3.setText((new Integer(local_p_dec_ini).toString())+" %");
	if (3 > maxspeed)
	  p_dec_s_3.enable(false);
	break;
      }
      case 4:{
	p_dec_s_4.setValue(local_p_dec_ini);
	p_dec_l_4.setText((new Integer(local_p_dec_ini).toString())+" %");
	if (4 > maxspeed)
	  p_dec_s_4.enable(false);
	break;
      }
      case 5:{
	p_dec_s_5.setValue(local_p_dec_ini);
	p_dec_l_5.setText((new Integer(local_p_dec_ini).toString())+" %");
	if (5 > maxspeed)
	  p_dec_s_5.enable(false);
	break;
      }
      case 6:{
	p_dec_s_6.setValue(local_p_dec_ini);
	p_dec_l_6.setText((new Integer(local_p_dec_ini).toString())+" %");
	if (6 > maxspeed)
	  p_dec_s_6.enable(false);
	break;
      }
      case 7:{
	p_dec_s_7.setValue(local_p_dec_ini);
	p_dec_l_7.setText((new Integer(local_p_dec_ini).toString())+" %");
	if (7 > maxspeed)
	  p_dec_s_7.enable(false);
	break;
      }
      case 8:{
	p_dec_s_8.setValue(local_p_dec_ini);
	p_dec_l_8.setText((new Integer(local_p_dec_ini).toString())+" %");
	if (8 > maxspeed)
	  p_dec_s_8.enable(false);
	break;
      }
      case 9:{
	p_dec_s_9.setValue(local_p_dec_ini);
	p_dec_l_9.setText((new Integer(local_p_dec_ini).toString())+" %");
	if (9 > maxspeed)
	  p_dec_s_9.enable(false);
	break;
      }
      }
    }

    super.pack();    
    super.show();
    close.requestFocus();
  }

  /////////////////
  // Fukui-Ishibashi
  public void show(double p_dec,int maxspeed,int maxspeed_array,int language){

    modeltype = 3;
    isRunning = true;

    switch(local_language){
    case 1:
      text.setText("          Fukui-Ishibashi-Modell\n          Regeln fuer paralleles Update\n          =============================\n\n- Fahrzeugposition p\n- Geschw. v und max. Geschw. v_max\n- Luecke g = Zahl der leeren Zellen zum Vordermann\n\n1. Beschleunigung\n       v <- MAX(v_max,g)\n2. Troedeln\n       Wenn v = v_max dann\n       mit Wahrsch. P(v_max): v <- max(v-1,0)\n3. Bewegung\n       p <- p+v");      super.setTitle("Infos & Details");
      break;
    default:
      text.setText("          Fukui-Ishibashi-model\n          Rules for the parallel update\n          ======================\n\n- Vehicle position p\n- Velocity v and Maximum Velocity v_max\n- Gap g = Amount of empty cells ahead\n\n1. Acceleration with respect to g\n     v <- MIN(v_max,g)\n2. Randomization\n     if v = v_max then\n       with prob. P(v_max) do v <- MAX(v-1,0)\n3. Movement\n     p <- p+v");
      super.setTitle("Info & Details");
      break;
    }
    local_p_dec = p_dec;
    local_p_dec_ini = (int)(100*p_dec);
    local_maxspeed = maxspeed;

    p_dec_s.setValue(local_p_dec_ini);
    p_dec_l.setText((new Integer(local_p_dec_ini).toString())+" %");

    super.pack();    
    super.show();
    close.requestFocus();
  }

}


