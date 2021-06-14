package es.uji.al375496.cultivemanagement

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import es.uji.al375496.cultivemanagement.model.MainModel
import es.uji.al375496.cultivemanagement.presenter.MainPresenter
import es.uji.al375496.cultivemanagement.view.MainView

class MainActivity : AppCompatActivity(), MainView {
    private lateinit var sectorsTextView: TextView
    private lateinit var subsectorsTextView: TextView
    private lateinit var sectorsEditText: EditText
    private lateinit var subsectorsEditText: EditText
    private lateinit var button: Button

    private lateinit var loadingTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar

    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "$title: Setup"

        sectorsTextView = findViewById(R.id.sectorsTextView)
        subsectorsTextView = findViewById(R.id.subsectorsTextView)
        sectorsEditText = findViewById(R.id.sectorsEditTextNumber)
        subsectorsEditText = findViewById(R.id.subsectorsEditTextNumber)
        button = findViewById(R.id.setUpButton)

        loadingTextView = findViewById(R.id.loadingTextView)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        val model =
            if (savedInstanceState != null) savedInstanceState.getParcelable<MainModel>(MODEL)!!
            else MainModel()
        presenter = MainPresenter(this, model, this)

        val textChangedWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { presenter.setCurrentValues(sectorsEditText.text.toString(), subsectorsEditText.text.toString())}
        }
        sectorsEditText.addTextChangedListener(textChangedWatcher)
        subsectorsEditText.addTextChangedListener(textChangedWatcher)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
    }

    override fun onButton(view: View) {
        presenter.setup()
    }

    override fun enableButton() {
        button.isEnabled = true
    }

    override fun disableButton() {
        button.isEnabled = false
    }

    override fun endLoading(sectors: String, subsectors: String) {
        sectorsEditText.setText(sectors)
        subsectorsEditText.setText(subsectors)

        sectorsTextView.visibility = View.VISIBLE
        subsectorsTextView.visibility = View.VISIBLE
        sectorsEditText.visibility = View.VISIBLE
        subsectorsEditText.visibility = View.VISIBLE
        button.visibility = View.VISIBLE

        loadingTextView.visibility = View.INVISIBLE
        loadingProgressBar.visibility = View.INVISIBLE
    }

    override fun startLoading() {
        sectorsTextView.visibility = View.INVISIBLE
        subsectorsTextView.visibility = View.INVISIBLE
        sectorsEditText.visibility = View.INVISIBLE
        subsectorsEditText.visibility = View.INVISIBLE
        button.visibility = View.INVISIBLE

        loadingTextView.visibility = View.VISIBLE
        loadingProgressBar.visibility = View.VISIBLE
    }

    override fun launchSectorSelectionActivity(){
        val intent = Intent(this, SectorSelectionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        outState.putParcelable(MODEL, presenter.model)
        super.onSaveInstanceState(outState)
    }
}