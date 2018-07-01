package feedme;

import org.junit.Assert;
import org.junit.Test;

public class TypesTests {

    Types types = new Types("types.xml");

    @Test
    public void createTypes_NotNull(){
        Assert.assertNotNull(types.getList());
    }

    @Test
    public void createTypes_correctSize(){
        Assert.assertEquals(3, types.getList().size());
    }

    @Test
    public void createTypes_correctTypeNames(){
        Assert.assertEquals("event", types.getList().get(0).getName());
        Assert.assertEquals("market", types.getList().get(1).getName());
        Assert.assertEquals("outcome", types.getList().get(2).getName());
    }

    @Test
    public void createTypes_correctHeaderFieldSize(){
        Assert.assertEquals(4, types.getList().get(0).getHeaderMap().size());
        Assert.assertEquals(4, types.getList().get(1).getHeaderMap().size());
        Assert.assertEquals(4, types.getList().get(2).getHeaderMap().size());
    }

    @Test
    public void createTypes_correctBodyFieldSize(){
        Assert.assertEquals(7, types.getList().get(0).getBodyMap().size());
        Assert.assertEquals(5, types.getList().get(1).getBodyMap().size());
        Assert.assertEquals(6, types.getList().get(2).getBodyMap().size());
    }

}
