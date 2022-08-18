import React, { Component } from 'react';
import ReactDOM from 'react-dom/client';
import PulseLoader from "react-spinners/PulseLoader"

class MovieForm extends React.Component {
  render() {
    const isLoading = (this.props.movieListIsLoaded === 'loading');
    console.log("currently loading...");

    return (
      <div>
        <form onSubmit={this.props.handeSubmit}>
          <label>
            <input type="text" value={this.props.value} onChange={this.props.handleChange} placeholder={"Enter movie title"} />

            {/* <input type="submit" /> */}
            <button type="submit" className="button-hide">
              <i className="fa fa-search fa-lg"></i>
            </button>
          </label>
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