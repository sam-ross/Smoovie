import React from "react";
import githubFooter from "../resources/github-footer.png"

class Footer extends React.Component {

  render() {
    const isLoaded = this.props.isLoaded;

    if (isLoaded === 'waiting') {
      console.log("waiting (footer)");
    } else if (isLoaded === 'loading') {
      console.log("loading!!!!! (footer)");
    } else {
      return (
        <section className="section-footer">
          <a href="https://github.com/sam-ross/smoovie-front-end" target="_blank" rel="noreferrer">
            <img src={githubFooter} alt="github-white" className="footer-image"></img>
          </a>
        </section>
      )

    }
  }

}

export default Footer;