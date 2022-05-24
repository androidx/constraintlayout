@file:OptIn(ExperimentalMotionApi::class)

package androidx.demo.motiondemos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.demo.motiondemos.ui.theme.MotionDemosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotionDemosTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ProductScreen( )
                }
            }
        }
    }
}



@Preview(group = "foo")
@Composable
public fun ProductScreen() {
    ConstraintLayout(
        ConstraintSet("""
{
    Helpers: [
        ['hChain', ['topButton0','topButton1','topButton2','topButton3','topButton4'], {
            start: ['parent', 'start',132], end: ['search', 'start'], style: 'spread'}],
        ['vChain', ['pitch','pitchSub','addToCart'], {
            style: 'packed'}],
            ['hChain', ['baseCard1','baseCard2','baseCard3'], {
            start: ['parent', 'start',0], end: ['parent', 'end',0], style: 'spread'}],
        ],
    menu: { helper: 'hChain', ref: ['topButton0','topButton1','topButton2','topButton3','topButton4'],}, 
 
    
    topButton0: {   top: ['getStarted', 'top'], bottom: ['getStarted', 'bottom'] },
    topButton1: {   top: ['topButton0', 'top'],  },
    topButton2: {   top: ['topButton1', 'top'],  },
    topButton3: {   top: ['topButton2', 'top'],  },
    topButton4: {   top: ['topButton3', 'top'],  },
    topLogo: {
        width: 'wrap', height: 'wrap',
        start: ['parent', 'start',32],
        top: ['getStarted', 'top'], 
        bottom: ['getStarted', 'bottom'],
        rotationX: 360,
        
    },
    getStarted: { 
        width: 'wrap', height: 'wrap',
        end: ['parent', 'end', 32],
        top: ['parent', 'top', 16],
    },     
    cart : {
        end: ['getStarted', 'start'],
        top: ['getStarted', 'top'],
        bottom: ['getStarted', 'bottom'],
    },
    search : {
        width: 'wrap', height: 'wrap',
        end: ['cart', 'start'],
        top: ['cart', 'top'],
    },
     pitch : {start: ['parent', 'start',44] },
     pitchSub : {start: ['pitch', 'start']} ,
     addToCart : {start: ['pitchSub', 'start']},
     iconicImage : {start: ['addToCart', 'end',10], centerVertically: 'addToCart'},
     details : {start: ['iconicImage', 'end',10], centerVertically: 'addToCart'},
     background:  {  center: 'parent',scaleX: 0.8, scaleY: 0.8, translationX:36, rotationZ: 39 ,alpha: 0.5},
     product:  {  center: 'parent' },
     examples: { 
        width: 132,
        height: 132,
        end: ['parent' , 'end',64],
        top: ['arrow', 'bottom'],
        bottom: ['arrow', 'bottom']
    },
    arrow: {
        width: 'spread',
        height: 50,
        start: ['product','end'] ,
        top: ['product','bottom'],
        end: ['examples', 'start']
           
    },
    review: { 
    width: 200,
     height: 90,
     bottom: ['examples', 'top'],
      top: ['getStarted', 'bottom'],
        end: ['examples', 'end']
    },
    baseCard1: {bottom: ['parent' , 'bottom',32]},
    baseCard2: {bottom: ['baseCard1' , 'bottom']},
    baseCard3: {bottom: ['baseCard2' , 'bottom']},
   
     
}   
        """),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val icons = arrayOf( R.drawable.top_logo, R.drawable.search, R.drawable.cart)
        val iconsId = arrayOf( "topLogo", "search", "cart")
        val description = arrayOf( "top Logo", "search", "cart")

        for (i in 0..2) {
            Icon(
                modifier = Modifier.layoutId(iconsId[i]),
                painter = painterResource(id = icons[i]),
                contentDescription = description[i]
            )
        }
        Button( modifier = Modifier.layoutId("getStarted"),
            onClick = { /*TODO*/ }) {
            Text(text = "Get Started")
            
        }
        val topButton = arrayOf("Home", "About", "Products", "doc", "support")

        for (i in 0..4) {
            Text( modifier = Modifier.layoutId("topButton$i"),text = topButton[i])
        }

        Image(
            modifier = Modifier.layoutId("background"),
            painter = painterResource(id = R.drawable.g_flame),
            contentDescription = "flame"
        )

        Image( modifier = Modifier.layoutId("product"),
            painter = painterResource(id = R.drawable.cl_icon),
            contentDescription = ""
        )

        // ========================= Pitch
        Text(
            modifier = Modifier.layoutId("pitch"),
            fontSize = 32.sp,
            text = "State of art \nRelational Layout \nManagement ")

        Text( modifier = Modifier.layoutId("pitchSub"),
            fontSize = 22.sp,
            text = "The ui possibilities are \nendless")

        Button( modifier = Modifier.layoutId("addToCart"),
            onClick = { /*TODO*/ }) {
            Icon(
                painter = painterResource(id = R.drawable.cart),
                contentDescription = "cart"
            )
            Text(text = "Add To Cart")
        }


        Icon(
            modifier = Modifier.layoutId("iconicImage"),
            painter = painterResource(id = R.drawable.cart),
            contentDescription = "cart"
        )

        Text( modifier = Modifier.layoutId("details"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            text = "details >"
        )

        Canvas(modifier =  Modifier.layoutId("arrow")) {
            val w = size.width
            val h = size.height
            val arrowSize = 40;
            val arrowPath = Path();
            arrowPath.moveTo(0f,0f);
            arrowPath.cubicTo(w/6,w/6, 0f,h,w,h)
            arrowPath.lineTo(w-arrowSize,h-arrowSize)
            arrowPath.lineTo(w,h)
            arrowPath.lineTo(w-arrowSize,h+arrowSize)
            drawPath(
                path=arrowPath,
                color = Color.Blue,
                style =  Stroke(10f),
            )
        }
        Surface(
            contentColor = Color(0xFFFFFFFF),
            modifier = Modifier.layoutId("examples"),
            elevation = 18.dp,
            shape = RoundedCornerShape(8.dp)
        ) {

        }
        Surface(

            modifier = Modifier.layoutId("review"),
            elevation = 18.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column( modifier = Modifier.fillMaxWidth()
                .padding(12.dp))  {
                Text(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    text = "Best Layout Engine"
                )
                Text(
                    fontSize = 14.sp,
                    text = "Compose + ConstraintLayout\nMakes Life easy"
                )
            }

        }

        CallOut( "baseCard1", "Excellent support", "Twitter, Stack overflow, github")
        CallOut("baseCard2","Open Source", "Developed on github" )
        CallOut("baseCard3","Compose or ViewGroup","Committed to evolving both")
//        Icon(
//            modifier = Modifier.layoutId("product"),
//            painter = painterResource(id = R.drawable.buds),
//            contentDescription = "buds"
//        )

    }
}

@Composable
public fun CallOut(id:String, feature:String,sub:String) {
    Row(modifier=Modifier.layoutId(id),verticalAlignment  = Alignment.CenterVertically)  {
        Box(modifier = Modifier.background(Color.Yellow)) {
            Icon(
                modifier = Modifier.layoutId("iconicImage"),
                painter = painterResource(id = R.drawable.cart),
                contentDescription = "cart"
            )
        }

    Column( modifier = Modifier
        .padding(12.dp))  {
        Text(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            text = feature
        )
        Text(
            fontSize = 14.sp,
            text = sub
        )
    }
    }
}

