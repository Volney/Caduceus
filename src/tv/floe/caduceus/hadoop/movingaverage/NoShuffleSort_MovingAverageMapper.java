package tv.floe.caduceus.hadoop.movingaverage;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.log4j.Logger;

//import tv.floe.caduceus.hadoop.movingaverage.MovingAverageMapper.Timeseries_Counters;

public class NoShuffleSort_MovingAverageMapper  extends MapReduceBase implements Mapper<LongWritable, Text, TimeseriesKey, TimeseriesDataPoint> 
{

	   
	static enum Timeseries_Counters { BAD_PARSE, BAD_LOOKUP };

	   
	   private JobConf configuration;
	   private final TimeseriesKey key = new TimeseriesKey();
	   private final TimeseriesDataPoint val = new TimeseriesDataPoint();
	   

	   private static final Logger logger = Logger.getLogger( NoShuffleSort_MovingAverageMapper.class );

	   
	   public void close() {
		   
		   
	   }
	   
	   public void configure(JobConf conf) {
	      //logger.info("GenerateSAXSegments_s1_Mapper.configure()");
	      this.configuration = conf;
	      
	   }
	   
	   
	   
	   
	   @Override
	   public void map(LongWritable inkey, Text value, OutputCollector<TimeseriesKey, TimeseriesDataPoint> output, Reporter reporter) throws IOException {

	      String line = value.toString();
	      
	      YahooStockDataPoint rec = YahooStockDataPoint.parse( line );
	      
	      if (rec != null) {
	    	  
	    		  // set both parts of the key
	    		  key.set( rec.stock_symbol, rec.date );
	    	      
	    		  val.fValue = rec.getAdjustedClose();
	    	      val.lDateTime = rec.date;
	    	      
	    	      // now that its parsed, we send it through the shuffle for sort, onto reducers
	    	      output.collect(key, val);
	         
	      } else {
	    	  
	    	  reporter.incrCounter( Timeseries_Counters.BAD_PARSE, 1 );
	    	  
	      }
	    	  
	   }

}
