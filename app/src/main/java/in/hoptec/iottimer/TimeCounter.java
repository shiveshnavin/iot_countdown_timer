/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.hoptec.iottimer;
 
/**
 *
 * @author shivesh
 */
public class TimeCounter   {
    
    public long milis;
    public int minz,secz,milz;
    public TimeCounter(long mi)
    {
        milis=mi;
    }
    
    public long getMili()
    {
        int mili=0;
        
        int sec=getSec();
       // long elapsedMilis=(getHrs()*60*60*1000)+(getMin()*60*1000)+(getSec()*1000);
      long elapsedMilis=(getHrs()*60*60*1000)+(getMin()*60*1000)+(getSec()*1000);
 
       long curl=milis;
        
        //System.out.println(""+getHrs()+"*60*60*1000+"+getMin()+"*60*1000+"+getSec()+"*1000 = "+elapsedMilis);
        curl=curl-elapsedMilis ;
        
        mili=(int)(curl/1000);
       
        Double ml=new Double(milis);
        ml=ml/1000;
        int m=ml.intValue();
        ml=ml-m;
        ml=ml*1000;
        mili=ml.intValue();
        return mili;
    }
    public int getSec()
    {
        int sec=0; 
        int min=getMin();
        long elapsedSecs=getHrs()*60*60*1000+min*60*1000;
        long curs=milis;
        curs=curs-elapsedSecs;
        
        sec=(int)curs/1000;
       
        return sec;
    }
    
    public int getMin()
    {
        int min=0;
        int hrs=getHrs();
        long elapsedMins=hrs*60*60*1000;
        long curm=milis;
        curm=curm-elapsedMins;
        
        min=(int)curm/1000;
        min=(int)min/60;
        
        
        return min;
    }
     
    public int getHrs()
    {
        int hrs=0;
        Double tm=new Double(milis/1000);
        int tmi=tm.intValue();
        //got second
        tmi=tmi/60;
        //got min
        tmi=tmi/60;
        //got hrs
        hrs=tmi;
       
        
        
        return hrs;
    }
     
    public String fmt(int val)
    {
        String ftt=""+val;
        if(ftt.length()<2)
        {
            ftt="0"+ftt;
        }
        
        return ftt;
    }


    public String fmt(long val)
    {
        String ftt=""+val;
        if(ftt.length()<2)
        {
            ftt="0"+ftt;
        }
        if(ftt.length()<3)
        {
            ftt="0"+ftt;
        }

        return ftt;
    }



    public String getTimeString()
    {
       return ""+fmt(getMin())+":"+fmt(getSec())+":"+fmt(getMili());
       
    }

}
