import React, { Component } from 'react';
import ReactDOM from 'react-dom/client';
import PulseLoader from "react-spinners/PulseLoader"

class MovieForm extends React.Component {
  render() {
    const isLoading = (this.props.movieListIsLoaded === 'loading');
    // console.log("currently loading...");


    let value = this.props.value;
    const isDemoMode = this.props.demoMode;
    if (isDemoMode) {
      value = "Currently in demo mode";
    }

    return (
      <div>
        <form onSubmit={this.props.handeSubmit} id={"form-disabled-" + isDemoMode}>
          <fieldset disabled={isDemoMode}>
            <label>
              <input type="text" value={value} onChange={this.props.handleChange} placeholder={"Enter movie title"} id={"label-disabled-" + isDemoMode} />

              {/* <input type="submit" /> */}
              <button type="submit" className="button-hide">
                <i className="fa fa-search fa-lg" id={"i-disabled-" + isDemoMode}></i>
              </button>
            </label>
          </fieldset>
        </form>
        <div className='loading-spinner'>
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