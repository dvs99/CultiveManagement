package es.uji.al375496.cultivemanagement

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.uji.al375496.cultivemanagement.model.ShowNotesModel
import es.uji.al375496.cultivemanagement.model.database.entities.Note
import es.uji.al375496.cultivemanagement.model.database.entities.Sector
import es.uji.al375496.cultivemanagement.model.database.entities.Subsector
import es.uji.al375496.cultivemanagement.presenter.ShowNotesPresenter
import es.uji.al375496.cultivemanagement.view.NoteAdapter
import es.uji.al375496.cultivemanagement.view.ParcelableDialogInfo
import es.uji.al375496.cultivemanagement.view.ShowNotesView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.IOException


@Suppress("DEPRECATION")
@SuppressLint("InflateParams", "SimpleDateFormat", "CutPasteId")
class ShowNotesActivity : AppCompatActivity(), ShowNotesView {
    private lateinit var renameButton: Button
    private lateinit var completeButton: Button
    private lateinit var addButton: Button
    private var shouldShowRenameButton = true
    private var shouldShowAddButton = true

    private lateinit var pendingRecyclerView: RecyclerView
    private lateinit var completeRecyclerView: RecyclerView
    private val pendingList: MutableList<Note> = mutableListOf()
    private val completeList: MutableList<Note> = mutableListOf()
    private var shouldShowPendingRecyclerView = true

    private lateinit var loadingTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar

    private lateinit var presenter: ShowNotesPresenter
    private var storedDialog: ParcelableDialogInfo? = null

    private var currentPhotoPath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_notes)

        renameButton = findViewById(R.id.renameButton)
        completeButton = findViewById(R.id.completeButton)
        addButton = findViewById(R.id.addButton)

        pendingRecyclerView = findViewById(R.id.pendingRecyclerView)
        completeRecyclerView = findViewById(R.id.completeRecyclerView)

        loadingTextView = findViewById(R.id.loadingTextView)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        pendingRecyclerView.adapter = NoteAdapter(pendingList, ::onElementClick)
        pendingRecyclerView.layoutManager = LinearLayoutManager(this)
        completeRecyclerView.adapter = NoteAdapter(completeList, ::onElementClick)
        completeRecyclerView.layoutManager = LinearLayoutManager(this)

        val model =
            if (savedInstanceState != null) savedInstanceState.getParcelable(MODEL)!!
            else ShowNotesModel()
        presenter = ShowNotesPresenter(this, model, this)

        val sector: Sector? = intent.getParcelableExtra(SECTOR)
        val subsector: Subsector? = intent.getParcelableExtra(SUBSECTOR)
        if (sector != null) {
            if (subsector != null)
                presenter.setup(sector, subsector)
            else
                presenter.setup(sector)
        }
        else
            presenter.setup()

        storedDialog = savedInstanceState?.getParcelable(DIALOG)
    }


    override fun onResume() {
        super.onResume()
        storedDialog?.let {
            restoreDialog(it)
            storedDialog = null
        }
    }

    override var loading: Boolean
        get() = loadingProgressBar.visibility == View.VISIBLE
        set(value) {
            if (value){
                renameButton.visibility = View.GONE
                completeButton.visibility = View.GONE
                addButton.visibility = View.GONE

                pendingRecyclerView.visibility = View.GONE
                completeRecyclerView.visibility = View.GONE

                loadingTextView.visibility = View.VISIBLE
                loadingProgressBar.visibility = View.VISIBLE
            }
            else {
                if (shouldShowRenameButton)
                    renameButton.visibility = View.VISIBLE
                completeButton.visibility = View.VISIBLE
                if (shouldShowAddButton)
                    addButton.visibility = View.VISIBLE

                if (shouldShowPendingRecyclerView){
                    pendingRecyclerView.visibility = View.VISIBLE
                    completeRecyclerView.visibility = View.GONE
                }
                else {
                    pendingRecyclerView.visibility = View.GONE
                    completeRecyclerView.visibility = View.VISIBLE
                }
                loadingTextView.visibility = View.INVISIBLE
                loadingProgressBar.visibility = View.INVISIBLE
            }
        }

    override var canRename: Boolean
        get() = shouldShowRenameButton
        set(value) {
            shouldShowRenameButton = value
            if (value)
                renameButton.visibility = View.VISIBLE
            else
                renameButton.visibility = View.GONE
        }

    override var canAdd: Boolean
        get() = shouldShowAddButton
        set(value) {
            shouldShowAddButton = value
            if (value)
                addButton.visibility = View.VISIBLE
            else
                addButton.visibility = View.GONE
        }
    override var showPending: Boolean
        get() = shouldShowPendingRecyclerView
        set(value) {
            shouldShowPendingRecyclerView = value
            if (value){
                completeButton.text = getString(R.string.show_completed)
                if (! loading){
                    pendingRecyclerView.visibility = View.VISIBLE
                    completeRecyclerView.visibility = View.GONE
                }
            }
            else {
                if (! loading){
                    completeButton.text = getString(R.string.show_pending)
                    pendingRecyclerView.visibility = View.GONE
                    completeRecyclerView.visibility = View.VISIBLE
                }
            }
        }

    override fun onRename(view: View) {
        val ctx: Context = this
        val builder = AlertDialog.Builder(ctx).apply {
            setTitle(getString(R.string.enter_name))
            val input = EditText(ctx)
            input.apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                maxLines = 1
                gravity = Gravity.CENTER
            }
            setView(input)
            setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss()
                presenter.rename(input.text.toString())
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onAdd(view: View) {
            restoreDialog(ParcelableDialogInfo("", "", null, null))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && storedDialog != null && currentPhotoPath != null) {
            var bitmap: Bitmap? = null
            try {
                bitmap = if (Build.VERSION.SDK_INT < 28) MediaStore.Images.Media.getBitmap(contentResolver, currentPhotoPath)
                    else ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, currentPhotoPath!!))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            storedDialog!!.image = bitmap
        }
    }
    override fun onSwitchRecyclerView(view: View) {
        presenter.switchRecyclerView()
    }

    override fun populateRecycleViews(pendingNotes: List<Note>, completeNotes: List<Note>) {
        pendingList.clear()
        pendingList.addAll(pendingNotes)
        pendingRecyclerView.adapter?.notifyDataSetChanged()

        completeList.clear()
        completeList.addAll(completeNotes)
        completeRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun setTitle(string: String) {
        title = string
    }



    private fun restoreDialog(info: ParcelableDialogInfo){
        val ctx: Context = this
        val builder = AlertDialog.Builder(ctx).apply {
            setTitle(getString(R.string.add_new_note))
            val layout: View = layoutInflater.inflate(R.layout.dialog_add, null)
            setView(layout)
            setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss()
                presenter.addNote(
                    layout.findViewById<EditText>(R.id.addTitleEditText).text.toString(),
                    layout.findViewById<EditText>(
                        R.id.addNoteEditTextMultiLine
                    ).text.toString(),
                    currentPhotoPath.toString()
                )
                currentPhotoPath = null
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()

        dialog.findViewById<TextView>(R.id.addTitleEditText)?.text = info.title
        dialog.findViewById<TextView>(R.id.addNoteEditTextMultiLine)?.text = info.text
        if (info.image != null){
            dialog.findViewById<Button>(R.id.pictureButton)?.visibility = View.INVISIBLE
            dialog.findViewById<ImageView>(R.id.previewImageView)?.visibility = View.VISIBLE
            dialog.findViewById<ImageView>(R.id.previewImageView)?.setImageBitmap(info.image)
        }
        val permissionCheckA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val permissionCheckB = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheckA != PackageManager.PERMISSION_GRANTED || (permissionCheckB != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < 29))
            dialog.findViewById<Button>(R.id.pictureButton)?.isEnabled = false


        dialog.findViewById<TextView>(R.id.pictureButton)?.setOnClickListener {
            val permissionCam = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            val permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCam == PackageManager.PERMISSION_GRANTED && (permissionWrite == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= 29)) {
                storedDialog  = ParcelableDialogInfo(
                    dialog.findViewById<EditText>(R.id.addTitleEditText)!!.text.toString(),
                    dialog.findViewById<EditText>(
                        R.id.addNoteEditTextMultiLine
                    )!!.text.toString(),
                    null,
                    null
                )

                dialog.dismiss()
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    // Ensure that there's a camera activity to handle the intent
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        // Create the File where the photo should go
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.TITLE, "MyPicture")
                        values.put(
                            MediaStore.Images.Media.DESCRIPTION,
                            "Photo taken on " + System.currentTimeMillis()
                        )
                        currentPhotoPath = contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoPath)
                        startActivityForResult(takePictureIntent, 0)

                    }
                }
            }
        }
    }

    private fun onElementClick(note: Note){
        val ctx : Context = this
        GlobalScope.launch(Dispatchers.Main) {
            val builder = AlertDialog.Builder(ctx).apply {
                setTitle(note.title)
                setView(R.layout.dialog_note)
                setPositiveButton(getString(R.string.back)) { dialog, _ ->
                    dialog.dismiss()
                }

                setNeutralButton(getString(R.string.delete)){ dialog, _ ->
                    presenter.deleteNote(note)
                      dialog.dismiss()
                }

                if (!note.completed){
                    setNegativeButton(getString(R.string.set_complete)) { dialog, _ ->
                        presenter.completeNote(note)
                        dialog.dismiss()
                    }
                }
            }

            val dialog = builder.create()
            dialog.show()

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dialog.findViewById<TextView>(R.id.fullNoteTextView)?.text = note.text

            dialog.findViewById<TextView>(R.id.dialogTakenOnTimestamp)?.text = simpleDateFormat.format(note.creation_timestamp)
            if (note.reminder_timestamp != null)
                dialog.findViewById<TextView>(R.id.dialogReminderTimestamp)?.text = simpleDateFormat.format( note.creation_timestamp)
            else
            {
                dialog.findViewById<TextView>(R.id.dialogReminderTimestamp)?.visibility = View.GONE
                dialog.findViewById<TextView>(R.id.reminderTextView)?.visibility = View.GONE
            }
            if (note.imgUri != null){
                var bitmap: Bitmap? = null
                try {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    bitmap = if (Build.VERSION.SDK_INT < 28) MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(note.imgUri))
                    else ImageDecoder.decodeBitmap(ImageDecoder.createSource(ctx.contentResolver, Uri.parse(note.imgUri)))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (bitmap != null){
                    dialog.findViewById<ImageView>(R.id.noteImageView)?.visibility = View.VISIBLE
                    dialog.findViewById<ImageView>(R.id.noteImageView)?.setImageBitmap(bitmap)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        outState.putParcelable(MODEL, presenter.model)
        outState.putParcelable(DIALOG, storedDialog)
        super.onSaveInstanceState(outState)
    }
}

