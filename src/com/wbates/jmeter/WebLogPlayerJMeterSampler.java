package com.wbates.jmeter;

import com.wbates.weblog.*;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.io.FileNotFoundException;
import java.io.IOException;

public class WebLogPlayerJMeterSampler extends AbstractJavaSamplerClient
{
  public SampleResult runTest( JavaSamplerContext javaSamplerContext )
  {
    WebLogReader reader = null;
    StaticTime st = new StaticTime();

    boolean loopCtl = true;
    int logLinesSuccessfullyProcessed = 0;
    int logLinesFailedProcessing = 0;
    long startTimeMills = 0;

    // TODO check command-line arguments
    // TODO usage syntax

    try
    {
      reader = new WebLogReader( javaSamplerContext );

      while (loopCtl)
      {
        WebLogLine line = null;
        WebLogRecord record = null;

        try {
          line = reader.readLine();
        } catch (WebLogLineException w) {
          continue;
        }

        if ( line != null)
        {
          try
          {
            record = new WebLogRecord(line, st, javaSamplerContext);

          } catch (WebLogRecordException w)
          {
            System.out.println("Unable to create record: " + w);
            logLinesFailedProcessing++;
          }

          if (record != null)
          {
            if (record.getStatus() == 200)
            {
              //TODO fire and forget with some amount of threads.
              record.retrieve(st, javaSamplerContext);
              logLinesSuccessfullyProcessed++;
            }
          }

        } else
        {
          loopCtl = false;
        }
      }

      return this.createSampleResult( startTimeMills, logLinesSuccessfullyProcessed, logLinesFailedProcessing, true );

    } catch (FileNotFoundException fnf)
    {
      System.out.println("Unable to find web log: " + fnf);
      return this.createSampleResult( startTimeMills, logLinesSuccessfullyProcessed, logLinesFailedProcessing, false );

    } catch (IOException ioe)
    {
      System.out.println("Unable to retrieve line: " + ioe);
      return this.createSampleResult( startTimeMills, logLinesSuccessfullyProcessed, logLinesFailedProcessing, false );

    }
  }

  public Arguments getDefaultParameters()
  {
    Arguments arguments = new Arguments();
    arguments.addArgument( "LogFile", "/tmp/apache.log" );
    arguments.addArgument( "LogTimestampPattern", "dd/MMM/yyyy:HH:mm:ss" );
    arguments.addArgument( "HostName", "localhost" );
    arguments.addArgument( "Scheme", "http" );
    arguments.addArgument( "LogLinePattern", "([A-Z][a-z]{2} \\d{2} \\d{2}:\\d{2}:\\d{2} [\\w\\-\\.\\_]+ \\w+\\[\\d+\\]: [\\d\\.]+ \\-) \\[(\\d{2}\\/[A-Z][a-z]{2}\\/\\d{4}:\\d{2}:\\d{2}:\\d{2}) [\\+\\-]\\d{4}\\] ([a-zA-Z\\.\\-]+) \\\"([A-Z]+) (\\S+) (HTTP\\/[\\S]+) (\\d{3}) ([\\d\\-]+) \\\"\\S+\\\" (.+)" );
    arguments.addArgument( "FieldTimestamp", "2" );
    arguments.addArgument( "FieldPath", "5" );
    arguments.addArgument( "FieldStatus", "7" );
    arguments.addArgument( "TimeMultiplier", "1" );

    return arguments;
  }

  private SampleResult createSampleResult( long startTimeMills, int logLinesSuccessfullyProcessed, int logLinesFailedProcessing, boolean isSuccess )
  {
    SampleResult sampleResult = new SampleResult();

    if( isSuccess )
    {
      sampleResult.setSampleLabel( "Successfully processed: " + logLinesSuccessfullyProcessed + " of: " + (logLinesSuccessfullyProcessed + logLinesFailedProcessing) );
      sampleResult.setSuccessful( true );

    } else
    {
      sampleResult.setSampleLabel( "Error - Processed: " + logLinesSuccessfullyProcessed + " of: " + (logLinesSuccessfullyProcessed + logLinesFailedProcessing) );
      sampleResult.setSuccessful( false );
      sampleResult.setErrorCount( logLinesFailedProcessing );
    }

    sampleResult.setStampAndTime( startTimeMills, System.currentTimeMillis() );
    sampleResult.setTimeStamp( System.currentTimeMillis() );
    sampleResult.setEndTime( System.currentTimeMillis() );


    return sampleResult;
  }
}
