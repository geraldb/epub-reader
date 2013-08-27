package epub.reader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class EpubLibrary 
{
	String _booksPath;
	String _ctxPath;

	public EpubLibrary( String booksPath )
	{
		this( booksPath, "" );
	}
	
	public EpubLibrary( String booksPath, String ctxPath )
	{
		_booksPath = booksPath;
		_ctxPath   = ctxPath;   // web context path
		
		System.out.println( "[EpubLibrary.ctor] booksPath: " + booksPath );
		System.out.println( "[EpubLibrary.ctor] ctxPath: " + ctxPath );
	}
	
	public String fetchBooks() throws Exception
	{
		System.out.println( "call fetchBooks()" );

		File root = new File( _booksPath );
		
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept( File dir, String name ) 
			{
			   if( name.toLowerCase().endsWith( ".epub" )) 
				 return true;
			   else
				return false;			
			}
		};
		
		File books[] = root.listFiles( filter );

        StringBuilder buf = new StringBuilder();
        buf.append( "<h1>" + books.length + " Books</h1>\n");
        System.out.println( "=== " + _booksPath + " ===" );
		
        System.out.println( "[EpubLibrary.fetchBooks] ctxPath: " + _ctxPath );
        
		for( File book : books )
		{
			System.out.println( "book: " + book.getName() );
			System.out.println( "bookFullpath: " + book.getCanonicalPath() );
			
			String bookName = book.getName().replace(".epub", "");
			
			String line = String.format( "<a href='%s/%s'>%s</a>",
					_ctxPath,
					bookName,
					bookName );
			
			System.out.println( line );
			
			buf.append( "<p>" + line + "</p>\n" );
		}

		System.out.println( "=== end ===" );
		return buf.toString();
	}
	
	public String fetchBookContents( String bookName ) throws Exception
	{
		return fetchBookContents( bookName, false );  // filter by default - only show .xhtml|.html docs
	}
	
	public String fetchBookContents( String bookName, boolean showAllFlag ) throws Exception
	{
		System.out.println( "call fetchBookContents()" );
		
		String bookFullpath = _booksPath+"/"+bookName+".epub";
		System.out.println( "bookFullpath: " + bookFullpath );		
		
		ZipFile zipFile = new ZipFile( bookFullpath );

		Enumeration entries = zipFile.entries();
		
		StringBuilder buf = new StringBuilder();
        
        buf.append( "<h1>" + bookName + "</h1>\n" );
        System.out.println( "=== " + bookName + " ===" );
        
		while( entries.hasMoreElements() ) 
        {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            
            String line = String.format( "<a href='%s/%s/%s'>%s</a> | %d bytes | %TD",
            		_ctxPath,
            		bookName,
            		zipEntry.getName(),
                    zipEntry.getName(), 
                    zipEntry.getSize(),
                    new Date( zipEntry.getTime() ));

            System.out.println( line );
            
            if( zipEntry.getName().endsWith( "xhtml") ||
            	zipEntry.getName().endsWith( "html" ) ||
            	showAllFlag == true )
            {            
              buf.append( "<p>" + line + "</p>\n" );
            }
        }

		System.out.println( "=== end ===" );
		return buf.toString();
	}
	
	
	public void copyBookEntry( OutputStream out, String bookName, String entryPath ) throws Exception 
	{
		System.out.println( "call copyBookEntry()" );

		String bookFullpath = _booksPath+"/"+bookName+".epub";
		System.out.println( "bookFullpath: " + bookFullpath );		
	    
		System.out.println( "=== " + entryPath + " ===" );
		
		ZipFile zipFile = new ZipFile( bookFullpath );

		ZipEntry zipEntry = zipFile.getEntry( entryPath );
		            
        InputStream in = zipFile.getInputStream( zipEntry );

		int read = 0;
		byte[] bytes = new byte[1024];
		 
		while(( read = in.read( bytes )) != -1)  
		  out.write(bytes, 0, read);
		 
		System.out.println( "Done." );
	}
	
} // class EpubLibrary
