/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hierarchy.ranges;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author jimakos
 */
public class RangeDate {
    public Date lowerBound = null;
    public Date upperBound = null;
    
    public RangeDate(){
    
    }
    
    public RangeDate(Date _lowerBound, Date _upperBound){
        this.lowerBound = _lowerBound;
        this.upperBound = _upperBound;
    }

    public Date getLowerBound() {
        return lowerBound;
    }

    public Date getUpperBound() {
        return upperBound;
    }

    public void setLowerBound(Date lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void setUpperBound(Date upperBound) {
        this.upperBound = upperBound;
    }
    
    @Override 
    public String toString(){
       StringBuilder sb = new StringBuilder();

       sb.append(this.lowerBound);
       sb.append("-");
       sb.append(this.upperBound);

       return sb.toString();
    }     
    
    @Override
    public boolean equals(Object obj){
        
        if(obj == this)
            return true;
        if(obj == null || obj.getClass() != this.getClass())
            return false;
        RangeDate r = (RangeDate) obj;
        
        if (r.lowerBound == null){
            return true;
        }
        
        return (this.upperBound.equals(r.upperBound)) && (this.lowerBound.equals(r.lowerBound));
        
        
        
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    
    public boolean contains(Date v){
        //return v >= this.lowerBound && v <= this.upperBound;
        if (v.equals(this.lowerBound)){
            return true;
        }
        else if (v.equals(this.upperBound)){
            return true;
        }
        else if ( v.after(this.lowerBound) && v.before(this.upperBound)){
            return true;
        }
            
        return false;
        
        //return v.after(this.lowerBound) && v.before(this.upperBound);
    }
    
    public boolean contains2(Date v, boolean FLAG){
        if ( FLAG == true){
            //return v >= this.lowerBound && v < this.upperBound;
            if (v.equals(this.lowerBound)){
                return true;
            }
            else if ( v.after(this.lowerBound) && v.before(this.upperBound)){
                return true;
            }
            
            return false;
        }
        else{
            if (v.equals(this.lowerBound)){
                return true;
            }
            else if (v.equals(this.upperBound)){
                return true;
            }
            else if ( v.after(this.lowerBound) && v.before(this.upperBound)){
                return true;
            }
            return false;
            //System.out.println(" v = " + v + "\tmin = " + this.lowerBound + "\tmax = " + this.upperBound);
            //return v >= this.lowerBound && v <= this.upperBound;
        }
    }
    
    public String dateToString(int level){
        String str = null;
        Calendar calendar = Calendar.getInstance() ;
        if (lowerBound != null){

        
            calendar.setTime(lowerBound);

            Calendar calendar2 = Calendar.getInstance() ;

            calendar2.setTime(upperBound);

            switch (level) {
                case 0:
                    str = calendar.get(Calendar.YEAR) + "-" +calendar2.get(Calendar.YEAR);
                    break;
                case 1:
                    str = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + "-" + (calendar2.get(Calendar.MONTH)  + 1) + "/" + calendar2.get(Calendar.YEAR);
                    break;
                default:
                    str = calendar.get(Calendar.DAY_OF_MONTH) + "/" + ( calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + "-" + calendar2.get(Calendar.DAY_OF_MONTH) + "/" + (calendar2.get(Calendar.MONTH) + 1) + "/" + calendar2.get(Calendar.YEAR);
                    break;
            }
        }
        else{
            str = "(null)";
        }
        
        return str;
    }
    
    public String dateToExportHierString(int level){
        String str = null;
        Calendar calendar = Calendar.getInstance() ;
        if (lowerBound != null){

        
            calendar.setTime(lowerBound);

            Calendar calendar2 = Calendar.getInstance() ;

            calendar2.setTime(upperBound);

            switch (level) {
                case 0:
                    str = calendar.get(Calendar.YEAR) + "," +calendar2.get(Calendar.YEAR);					
                    break;
                case 1:
                    str = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + "," + (calendar2.get(Calendar.MONTH)  + 1) + "/" + calendar2.get(Calendar.YEAR);
                    break;
                default:
                    str = calendar.get(Calendar.DAY_OF_MONTH) + "/" + ( calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + "," + calendar2.get(Calendar.DAY_OF_MONTH) + "/" + (calendar2.get(Calendar.MONTH) + 1) + "/" + calendar2.get(Calendar.YEAR);
                    break;
            }
        }
        else{
            str = "null,null";
        }
        
        return str;
    }
    
    public static RangeDate parseRange(String str) throws ParseException{
        SimpleDateFormat sf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy" );
        String[] arr = str.split("-");
       
        Date lowBound = sf.parse(arr[0].trim());
        Date highBound = sf.parse(arr[1].trim());
        
        return new RangeDate(lowBound, highBound);
    }
    
}
