import android.content.res.Configuration
import com.henry.androidarchitecture.util.AppUtils
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class AppUtilsTest {
    @Test
    fun `shouldUseGridLayout returns true for landscape`() {
        val configuration = Configuration().apply {
            screenWidthDp = 800
            screenHeightDp = 400
        }
        
        assertTrue(AppUtils.shouldUseGridLayout(configuration))
    }
    
    @Test
    fun `shouldUseGridLayout returns true for tablet width`() {
        val configuration = Configuration().apply {
            screenWidthDp = 700
            screenHeightDp = 1000
        }
        
        assertTrue(AppUtils.shouldUseGridLayout(configuration))
    }
    
    @Test
    fun `shouldUseGridLayout returns false for portrait phone`() {
        val configuration = Configuration().apply {
            screenWidthDp = 400
            screenHeightDp = 800
        }
        
        assertFalse(AppUtils.shouldUseGridLayout(configuration))
    }
}