package org.ghrobotics

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.badlogic.gdx.utils.UBJsonReader
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import org.ghrobotics.objects.Field
import org.ghrobotics.objects.Robot

class Visualizer : ApplicationAdapter() {

    private val objLoader = ObjLoader()
    private val g3dModelLoader = G3dModelLoader(UBJsonReader())

    private lateinit var modelBatch: ModelBatch
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var topFieldViewport: Viewport
    private lateinit var guiViewport: Viewport

    private lateinit var topFieldView: PerspectiveCamera
    private lateinit var guiCamera: OrthographicCamera
    private lateinit var environment: Environment
    private lateinit var shadowLight: DirectionalShadowLight
    private lateinit var shadowBatch: ModelBatch

    private lateinit var fieldModel: Model
    private lateinit var robotModel: Model
    private lateinit var robotHistoryModel: Model

    private lateinit var world: World
    private lateinit var debugDrawer: DebugDrawer

    private lateinit var field: Field
    private lateinit var robot: Robot
    private lateinit var robotHistory: ModelInstance

    private lateinit var kanitFont: BitmapFont

    override fun create() {
        shapeRenderer = ShapeRenderer()

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        shadowLight = DirectionalShadowLight(1024, 1024, Constants.FIELD_LENGTH, Constants.FIELD_LENGTH, 1f, 100f)
        shadowLight.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f)
        environment.add(shadowLight)
        environment.shadowMap = shadowLight
        shadowBatch = ModelBatch(DepthShaderProvider())

        val modelBuilder = ModelBuilder()

        /*
        fieldModel = modelBuilder.createBox(Constants.FIELD_LENGTH, 0.1f, Constants.FIELD_WIDTH, Material(ColorAttribute.createDiffuse(Color.GRAY)),
                VertexAttributes.Usage.Position.toLong() or VertexAttributes.Usage.Normal.toLong())
*/

        fieldModel = objLoader.loadModel(Gdx.files.internal("field/field2.obj"))

        robotModel = modelBuilder.createBox(
            Constants.ROBOT_WIDTH, 1f, Constants.ROBOT_LENGTH, Material(ColorAttribute.createDiffuse(Color.BLUE)),
            VertexAttributes.Usage.Position.toLong() or VertexAttributes.Usage.Normal.toLong()
        )

        robotHistoryModel = modelBuilder.createSphere(
            0.1f, 0.1f, 0.1f, 5, 5, Material(ColorAttribute.createDiffuse(Color.RED)),
            VertexAttributes.Usage.Position.toLong() or VertexAttributes.Usage.Normal.toLong()
        )

        val freeTypeFontGenerator = FreeTypeFontGenerator(Gdx.files.internal("font/Kanit-Regular.ttf"))
        val freeTypeParameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        freeTypeParameter.size = 32
        kanitFont = freeTypeFontGenerator.generateFont(freeTypeParameter)

        modelBatch = ModelBatch()
        spriteBatch = SpriteBatch()

        topFieldView = PerspectiveCamera(80f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        topFieldView.position.set(Constants.FIELD_LENGTH / 2f, Constants.FIELD_WIDTH, Constants.FIELD_WIDTH)
        topFieldView.lookAt(Constants.FIELD_LENGTH / 2f, 0f, Constants.FIELD_WIDTH / 2f)
        topFieldView.up.set(Vector3.Y)
        topFieldView.near = 0.1f
        topFieldView.far = 300f
        topFieldView.update()

        guiCamera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        topFieldViewport = ScreenViewport(topFieldView)
        guiViewport = ScreenViewport(guiCamera)

        world = World()
        debugDrawer = DebugDrawer()
        world.dynamicsWorld.debugDrawer = debugDrawer
        debugDrawer.debugMode = btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE

        field = Field(fieldModel)
        robot = Robot(robotModel)
        robotHistory = ModelInstance(robotHistoryModel)

        field.collisionObject.worldTransform =
            Matrix4().setToTranslation(Vector3(Constants.FIELD_LENGTH / 2f, 0f, Constants.FIELD_WIDTH / 2f))
        robot.collisionObject.worldTransform = Matrix4().setToTranslation(Vector3(0f, 10f, 0f))

        world.addToWorld(field)
        world.addToWorld(robot)
    }

    override fun resize(width: Int, height: Int) {
        topFieldViewport.update(width, height)
        guiViewport.update(width, height)
    }

    override fun render() {

        if (Communications.reset) {
            RobotTracker.reset()
            Communications.reset = false
        }

        RobotTracker.update()

        val currentSegment = RobotTracker.currentRobotSegment
        robot.proceedToTransform(
            Vector3(currentSegment.location.x, 0f, currentSegment.location.y),
            Quaternion().setEulerAnglesRad(currentSegment.angle.toFloat() + Math.PI.toFloat() / 2f, 0f, 0f)
        )

        world.update()

        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        topFieldView.update()

        shadowLight.begin(Vector3.Zero, topFieldView.direction)
        shadowBatch.begin(shadowLight.camera)
        //shadowBatch.render(field.modelInstance, environment)
        shadowBatch.render(robot.modelInstance, environment)
        shadowBatch.end()
        shadowLight.end()

        modelBatch.begin(topFieldView)
        modelBatch.render(field.modelInstance, environment)
        modelBatch.render(robot.modelInstance, environment)
        modelBatch.end()

        debugDrawer.begin(topFieldView)
        world.dynamicsWorld.debugDrawWorld()
        debugDrawer.end()

        Gdx.gl.glLineWidth(20f)
        shapeRenderer.projectionMatrix = guiCamera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.LIGHT_GRAY
        RobotTracker.pathHistory.render()
        shapeRenderer.color = Color.RED
        RobotTracker.robotHistory.render()
        shapeRenderer.end()
        Gdx.gl.glLineWidth(1f)

        spriteBatch.projectionMatrix = guiCamera.combined
        spriteBatch.begin()
        kanitFont.color = Color.BLACK
        kanitFont.draw(
            spriteBatch,
            "Robot X: ${Communications.robotX}\n" +
                "Robot Y: ${Communications.robotY}",
            -guiCamera.viewportWidth / 2f, guiCamera.viewportHeight / 3f
        )
        spriteBatch.end()

    }

    private fun List<RobotHistorySegment>.render() {
        if (size >= 2) {
            val floatArray = FloatArray(size * 2)

            forEachIndexed { index, segment ->
                val vec3 = topFieldView.project(Vector3(segment.location.x, 0f, segment.location.y))
                floatArray[index * 2] = vec3.x - guiCamera.viewportWidth / 2f
                floatArray[index * 2 + 1] = vec3.y - guiCamera.viewportHeight / 2f
            }

            shapeRenderer.polyline(floatArray)
        }
    }

    override fun dispose() {
        fieldModel.dispose()
    }

    companion object {
        init {
            Bullet.init()
        }
    }

}
