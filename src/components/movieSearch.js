import React from 'react';
import PulseLoader from "react-spinners/PulseLoader"

class MovieForm extends React.Component {

  render() {
    let value = this.props.value;
    const isLoading = (this.props.movieListIsLoaded === 'loading');
    const isDemoMode = this.props.demoMode;

    if (isDemoMode) {
      value = "In demo mode";
    }

    return (
      <div>
        <form onSubmit={this.props.handeSubmit} id={"form-disabled-" + isDemoMode}>
          <fieldset disabled={isDemoMode}>
            <label>
              <input type="text" value={value} onChange={this.props.handleChange} placeholder={"Moovie title..."} id={"label-disabled-" + isDemoMode} />
              <button type="submit" className="button-hide" aria-label="Hidden button">
                <i className="icon-search" id={"i-disabled-" + isDemoMode}></i>
              </button>
            </label>
          </fieldset>
        </form>
        <div className='loading-row-search'>
          <PulseLoader
            size={15}
            color={"#fff"}
            loading={isLoading}
          />
        </div>
      </div>
    )
  }
}

export default MovieForm;